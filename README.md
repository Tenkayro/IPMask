<p align="center">
  <img src="branding/ipmask-icon-64.png" alt="IPMask logo" width="64" height="64">
</p>

# IPMask

IPMask hides the connected server address anywhere Minecraft renders text through its font system, including chat, signs, holograms, GUI text, and hover tooltips. It also masks multiplayer address fields, LAN entries, connection status, disconnect messages, and narration to help prevent accidental leaks in streams, screenshots, and recordings. Address fields remain editable, and a per-screen eye button reveals the address only when requested. Hidden addresses are not drawn as readable text, and copy/cut are blocked until the field is revealed.

The mod is client-side only. It never rewrites server entries or changes pinging and connection logic. Saved server names intentionally remain unchanged; an IP typed into a server name will still be visible there. Mod Menu is optional.

**Author:** Tenkayro  
**Java package:** `tenkayro.ipmask`

The focusable 20×20 eye controls match the address field height and use simple monochrome open-eye and crossed-eye sprites based on the supplied Font Awesome Free 7.3.1 SVGs. The source SVGs, including their attribution, are preserved in `branding/`.

## Supported version and toolchain

| Component | Version |
| --- | --- |
| Minecraft | 26.3 |
| Fabric Loader | 0.20.1 |
| Fabric API | 0.165.0+26.3 |
| Fabric Loom | 1.19.3 |
| Gradle wrapper | 9.8.2 |
| Java | 25 |
| Mod Menu (optional development runtime) | 21.0.0 |
| Mappings | Minecraft's Mojang names; no separate Yarn mappings |

These are pinned in `gradle.properties`. Minecraft 26.3's official version metadata specifies Java 25. Loom 1.19.3 supports the 26.3 game artifact; the game ships with Mojang-named classes for this version.

## Build

Install a Java 25 JDK, then run:

```shell
./gradlew build
```

On Windows, run `.\gradlew.bat build`. Final build artifacts, including the release and sources jars, are written to `.dist/`.

The same tasks are available through npm on Windows:

```shell
npm install
npm run start
npm run start:offline
npm run start:modmenu
npm run build
npm test
npm run clean
```

`npm run start` launches the development client without Mod Menu. After dependencies have been downloaded once, `npm run start:offline` skips Gradle's online dependency checks and may start faster on a slow connection; it fails if a required dependency is missing from the local cache. Use `npm run start:modmenu` when you want to test the optional Mod Menu integration. These scripts use the checked-in Gradle wrapper; install Node.js/npm and Java 25 first.

## Publishing a release

`npm run release` interactively selects a patch, minor, or major version bump. It builds the mod, commits and pushes the version change, uploads the jar to Modrinth with Minotaur, then creates a GitHub release with the same jar. Both services use the version number `mc<Minecraft>-<mod version>-fabric`, for example `mc26.3-26.1.1-fabric`.

Publishing requires a clean branch synchronized with its upstream, GitHub CLI authentication (`gh auth login`), an existing Modrinth project matching `modrinth_project_id` in `gradle.properties`, and a Modrinth token in `.env` or `MODRINTH_TOKEN`. Start from `.env.example` to configure the token. The prompt describes the public actions and requires confirmation before changing the version or publishing. **No release has been created by setting up this workflow.** If an upload fails after the version commit is pushed, that commit remains on the branch and the service that already accepted the upload may need manual follow-up.

To launch the development client with Mod Menu for optional integration checks:

```shell
./gradlew -PwithModMenu runClient
```

Mod Menu is added to the development runtime only when `-PwithModMenu` is passed, and is listed as a suggestion in Fabric metadata. Its update checker is disabled for this mod. It is not a compile or release dependency. Run `./gradlew runClient` without that property to test the mod without Mod Menu. Fabric API provides the resource loader used for IPMask's localized strings. Mod Menu 21.0.0 rejects `"client"` as a custom badge key, so its Client badge is supplied automatically by the mod's `"environment": "client"` metadata instead.

## Manual test checklist

- [x] Add Server: address starts hidden; typing, deleting, and pasting work; eye toggles visibility.
- [x] Edit Server: same behavior; saving without changes preserves the saved address.
- [x] Direct Connection: address starts hidden; typing and eye toggle work.
- [x] Server list: server names stay unchanged; IP addresses in LAN entries and hover tooltips are masked.
- [ ] Text matching the connected server address is masked across font-rendered text and narration.
- [x] Ctrl+C and Ctrl+X are blocked while an address field is hidden and work when it is visible.
- [x] Connecting and pinging still use the real address and behave like vanilla.
- [ ] Connecting screen masks the displayed IP while the connection uses the original address.
- [x] Reopening an address screen starts hidden again.
- [x] Mod Menu: icon, name, description, client badge, and links display correctly.
- [x] The mod loads without Mod Menu installed.
- [x] The built jar loads without errors in the log.

The checked items were confirmed in the Minecraft client before the latest connecting-screen change. That new masking behavior still needs an in-client check. The eye controls use custom GUI sprites, with a localized text fallback if a sprite is unavailable.

## Mixin targets

- `EditBox` — masks rendered field text and narration, and blocks copy/cut while hidden without changing editing.
- `ManageServerScreen` — adds the per-screen eye toggle to Add Server and Edit Server fields.
- `DirectJoinServerScreen` — adds the per-screen eye toggle to Direct Connection.
- `ServerSelectionList.OnlineServerEntry` — masks IP addresses in hover tooltip content without changing server names.
- `ServerSelectionList.NetworkServerEntry` — masks the address shown for LAN entries.
- `ConnectScreen` — masks the connecting status at render time and remembers the active display address for failure messages, without touching the connection.
- `Font` — masks matching active server addresses in prepared text, including GUI, chat, signs, and holograms while preserving text styles.
- `GameNarrator` — masks matching active server addresses in chat and system narration.
- `DisconnectedScreen` — masks matching address text in the error reason and its narration.
- `Screen` — exposes the inherited widget-registration method through a minimal invoker for the eye controls.

## Logo and project links

The supplied `IPMaskIcon.jpg` source is preserved in `branding/`. Optimized Lanczos-resized versions are used for the README header, the Fabric and Mod Menu icons, and the 512×512 Modrinth-ready `icon-512.png`.

Project links in `fabric.mod.json`: `https://modrinth.com/mod/ipmask`, `https://github.com/Tenkayro/IPMask`, and `https://github.com/Tenkayro/IPMask/issues`.

## Official version references

- [Minecraft version manifest](https://piston-meta.mojang.com/mc/game/version_manifest_v2.json)
- [Fabric developer versions](https://fabricmc.net/develop/)
- [Fabric Loader Maven metadata](https://maven.fabricmc.net/net/fabricmc/fabric-loader/maven-metadata.xml)
- [Fabric API Maven metadata](https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml)
- [Fabric Loom Maven metadata](https://maven.fabricmc.net/net/fabricmc/fabric-loom/maven-metadata.xml)
- [Gradle 9.8.2 release](https://docs.gradle.org/9.8.2/release-notes.html)
- [Mod Menu 26.3 versions](https://modrinth.com/mod/modmenu/versions)
- [Mod Menu metadata documentation](https://github.com/TerraformersMC/ModMenu/blob/26.3/README.md)

## License

MIT. See [LICENSE](LICENSE).
