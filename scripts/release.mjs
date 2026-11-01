import {
	cancel,
	confirm,
	intro,
	isCancel,
	note,
	outro,
	select,
	text,
} from "@clack/prompts";
import { spawn, spawnSync } from "node:child_process";
import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const root = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const isWindows = process.platform === "win32";
const gradle = join(root, isWindows ? "gradlew.bat" : "gradlew");

function stop(message = "Release cancelled.") {
	cancel(message);
	process.exit(0);
}

function unwrap(value) {
	if (isCancel(value)) stop();
	return value;
}

function readProperties(path) {
	return new Map(
		readFileSync(path, "utf8")
			.split(/\r?\n/u)
			.filter((line) => line.trim() && !line.trim().startsWith("#"))
			.map((line) => {
				const separator = line.indexOf("=");
				return separator > 0
					? [line.slice(0, separator).trim(), line.slice(separator + 1).trim()]
					: null;
			})
			.filter(Boolean),
	);
}

function loadDotEnv(env) {
	const file = join(root, ".env");
	if (!existsSync(file)) return env;
	for (const line of readFileSync(file, "utf8").split(/\r?\n/u)) {
		const match = line.match(/^\s*(?:export\s+)?([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*?)\s*$/u);
		if (!match || match[1] in env) continue;
		env[match[1]] = match[2].replace(/^(['"])(.*)\1$/u, "$2");
	}
	return env;
}

function bumpVersion(version, kind) {
	const match = version.match(/^(\d+)\.(\d+)\.(\d+)$/u);
	if (!match) throw new Error(`Unsupported version '${version}'; expected MAJOR.MINOR.PATCH.`);
	let [major, minor, patch] = match.slice(1).map(Number);
	if (kind === "major") {
		major += 1;
		minor = 0;
		patch = 0;
	} else if (kind === "minor") {
		minor += 1;
		patch = 0;
	} else {
		patch += 1;
	}
	return `${major}.${minor}.${patch}`;
}

function runSync(command, args, env = process.env) {
	const result = spawnSync(command, args, {
		cwd: root,
		env,
		encoding: "utf8",
		stdio: ["ignore", "pipe", "pipe"],
	});
	if (result.error) throw result.error;
	if (result.status !== 0) {
		throw new Error((result.stderr || result.stdout || `${command} failed`).trim());
	}
	return result.stdout.trim();
}

async function run(command, args, env = process.env) {
	const executable = isWindows && command.toLowerCase().endsWith(".bat")
		? (env.ComSpec || "cmd.exe")
		: command;
	const commandArgs = executable === command
		? args
		: ["/d", "/s", "/c", `call "${command}" ${args.map((arg) => `"${String(arg).replaceAll('"', '""')}"`).join(" ")}`];
	await new Promise((resolveRun, rejectRun) => {
		const child = spawn(executable, commandArgs, { cwd: root, env, stdio: "inherit" });
		child.once("error", rejectRun);
		child.once("close", (code) => {
			if (code === 0) resolveRun();
			else rejectRun(new Error(`${command} exited with code ${code}.`));
		});
	});
}

function ensureReleasePreconditions(repository, env) {
	const status = runSync("git", ["status", "--porcelain", "--untracked-files=normal"], env);
	if (status) throw new Error("Commit or stash all changes before releasing; the worktree must be clean.");
	const branch = runSync("git", ["branch", "--show-current"], env);
	if (!branch) throw new Error("Cannot release from a detached HEAD.");
	const upstream = runSync("git", ["rev-parse", "--abbrev-ref", "--symbolic-full-name", "@{upstream}"], env);
	const divergence = runSync("git", ["rev-list", "--left-right", "--count", `${upstream}...HEAD`], env)
		.split(/\s+/u)
		.map(Number);
	if (divergence[0] !== 0 || divergence[1] !== 0) {
		throw new Error("Push or synchronize the current branch with its upstream before releasing.");
	}
	const remote = runSync("git", ["remote", "get-url", "origin"], env)
		.replace(/\.git$/u, "")
		.replace(/\.git$/u, "");
	const normalizedRemote = remote
		.replace(/^https:\/\/github\.com\//u, "")
		.replace(/^git@github\.com:/u, "");
	if (normalizedRemote.toLowerCase() !== repository.toLowerCase()) {
		throw new Error(`Git origin is '${normalizedRemote}', but gradle.properties configures '${repository}'.`);
	}
	runSync("gh", ["auth", "status", "--hostname", "github.com"], env);
	return branch;
}

async function ensureModrinthProject(projectId, token) {
	const response = await fetch(`https://api.modrinth.com/v2/project/${encodeURIComponent(projectId)}`, {
		headers: {
			Authorization: token,
			"User-Agent": "Tenkayro/IPMask release tooling",
		},
	});
	if (!response.ok) {
		throw new Error(`Could not access Modrinth project '${projectId}' (HTTP ${response.status}). Verify its ID and token.`);
	}
}

async function ensureModrinthVersionAvailable(projectId, versionNumber, token) {
	const response = await fetch(`https://api.modrinth.com/v2/project/${encodeURIComponent(projectId)}/version`, {
		headers: {
			Authorization: token,
			"User-Agent": "Tenkayro/IPMask release tooling",
		},
	});
	if (!response.ok) {
		throw new Error(`Could not check existing Modrinth versions (HTTP ${response.status}).`);
	}
	const versions = await response.json();
	if (versions.some((version) => version.version_number === versionNumber)) {
		throw new Error(`Modrinth version '${versionNumber}' already exists.`);
	}
}

async function main() {
	intro("IPMask Release");
	const propertiesPath = join(root, "gradle.properties");
	const originalProperties = readFileSync(propertiesPath, "utf8");
	const properties = readProperties(propertiesPath);
	const currentVersion = properties.get("version");
	const minecraftVersion = properties.get("minecraft_version");
	const projectId = properties.get("modrinth_project_id");
	const repository = properties.get("github_repository");
	const env = loadDotEnv({ ...process.env });

	if (!currentVersion || !minecraftVersion || !projectId || !repository) {
		throw new Error("Set version, minecraft_version, modrinth_project_id, and github_repository in gradle.properties.");
	}
	if (!env.MODRINTH_TOKEN) throw new Error("Set MODRINTH_TOKEN in your environment or ignored .env file.");
	const branch = ensureReleasePreconditions(repository, env);

	const bump = unwrap(await select({
		message: `Choose release type (current ${currentVersion})`,
		options: [
			{ value: "patch", label: `Patch — ${bumpVersion(currentVersion, "patch")}` },
			{ value: "minor", label: `Minor — ${bumpVersion(currentVersion, "minor")}` },
			{ value: "major", label: `Major — ${bumpVersion(currentVersion, "major")}` },
		],
	}));
	const version = bumpVersion(currentVersion, bump);
	const releaseId = `mc${minecraftVersion}-${version}-fabric`;
	const releaseTag = releaseId;
	const changelog = unwrap(await text({
		message: "Short release notes",
		validate: (value) => value.trim().length >= 3 ? undefined : "Enter at least one change.",
	})).trim();

	await ensureModrinthProject(projectId, env.MODRINTH_TOKEN);
	await ensureModrinthVersionAvailable(projectId, releaseId, env.MODRINTH_TOKEN);
	const existingReleases = JSON.parse(runSync("gh", [
		"release", "list", "--repo", repository, "--limit", "1000", "--json", "tagName",
	], env));
	if (existingReleases.some((release) => release.tagName === releaseTag)) {
		throw new Error(`GitHub release '${releaseTag}' already exists.`);
	}
	note(
		[
			`Release:   ${releaseId}`,
			`Modrinth:  ${projectId}`,
			`GitHub:    ${repository} (${branch})`,
			"Actions:   Build, commit and push version, upload to Modrinth, create GitHub release",
		].join("\n"),
		"Public release summary",
	);
	if (!unwrap(await confirm({
		message: "This will publish publicly to Modrinth and GitHub. Continue?",
		initialValue: false,
	}))) stop("Nothing was published.");

	const versionLine = /^version=.*$/mu;
	if (!versionLine.test(originalProperties)) throw new Error("Could not locate version= in gradle.properties.");
	const notesPath = join(root, "build", "release-notes.md");
	const jarPath = join(root, ".dist", `ipmask-${version}.jar`);
	mkdirSync(join(root, "build"), { recursive: true });
	writeFileSync(notesPath, `${changelog}\n`, "utf8");
	writeFileSync(propertiesPath, originalProperties.replace(versionLine, `version=${version}`), "utf8");

	let versionCommitted = false;
	let versionStaged = false;
	try {
		await run(gradle, ["-PreleaseVersion=" + version, "build", "--no-daemon"], env);
		if (!existsSync(jarPath)) throw new Error(`Built release jar not found: ${jarPath}`);

		await runSync("git", ["add", "gradle.properties"], env);
		versionStaged = true;
		await runSync("git", [
			"commit",
			"-m", `Release ${releaseTag}`,
			"-m", "Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>",
		], env);
		versionCommitted = true;
		await run("git", ["push", "origin", branch], env);

		await run(gradle, ["-PreleaseVersion=" + version, "modrinth", "--no-daemon"], env);
		await run("gh", [
			"release", "create", releaseTag, jarPath,
			"--repo", repository,
			"--target", branch,
			"--title", `IPMask ${version} for Fabric ${minecraftVersion}`,
			"--notes-file", notesPath,
		], env);
	} catch (error) {
		if (!versionCommitted) {
			if (versionStaged) runSync("git", ["restore", "--staged", "--", "gradle.properties"], env);
			writeFileSync(propertiesPath, originalProperties, "utf8");
		}
		throw error;
	}

	outro(`Published ${releaseId} to Modrinth and GitHub.`);
}

main().catch((error) => {
	cancel(error.message || String(error));
	process.exitCode = 1;
});
