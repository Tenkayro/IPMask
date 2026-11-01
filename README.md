<p align="center">
  <img src="branding/ipmask-icon-64.png" alt="IPMask logo" width="64" height="64">
</p>

# IPMask

IPMask hides server IP addresses in multiplayer menus to help prevent accidental leaks in streams, screenshots, and recordings. Address fields remain editable, and a per-screen eye button reveals the address only when requested. Hidden addresses are not drawn as readable text, and copy/cut are blocked until the field is revealed.

The mod is client-side only. It never rewrites server entries or changes pinging and connection logic. Mod Menu is optional.

**Author:** Tenkayro  
**Java package:** `tenkayro.ipmask`

The focusable 24×24 eye controls use custom high-contrast open-eye and crossed-eye sprites.

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

On Windows, run `.\gradlew.bat build`. The release jar is written to `build/libs/`.

The same tasks are available through npm on Windows:

```shell
npm run start
npm run build
npm test
npm run clean
```

`npm run start` always starts the development client with Mod Menu. These scripts use the checked-in Gradle wrapper; install Node.js/npm and Java 25 first.

To launch the development client with Mod Menu for optional integration checks:

```shell
./gradlew -PwithModMenu runClient
```

Mod Menu is added to the development runtime only when `-PwithModMenu` is passed, and is listed as a suggestion in Fabric metadata. Its update checker is disabled for this mod. It is not a compile or release dependency. Run `./gradlew runClient` without that property to test the mod without Mod Menu. Fabric API provides the resource loader used for IPMask's localized strings. Mod Menu 21.0.0 rejects `"client"` as a custom badge key, so its Client badge is supplied automatically by the mod's `"environment": "client"` metadata instead.

## Manual test checklist

- [x] Add Server: address starts hidden; typing, deleting, and pasting work; eye toggles visibility.
- [x] Edit Server: same behavior; saving without changes preserves the saved address.
- [x] Direct Connection: address starts hidden; typing and eye toggle work.
- [x] Server list: address portions in names and entry hover tooltips are masked; the list eye toggle reveals/hides them.
- [x] Ctrl+C and Ctrl+X are blocked while an address field is hidden and work when it is visible.
- [x] Connecting and pinging still use the real address and behave like vanilla.
- [x] Reopening an address screen starts hidden again.
- [x] Mod Menu: icon, name, description, client badge, and links display correctly.
- [x] The mod loads without Mod Menu installed.
- [x] The built jar loads without errors in the log.

The author confirmed the interactive checklist in the Minecraft client. The eye controls use custom GUI sprites, with a localized text fallback if a sprite is unavailable.

## Mixin targets

- `EditBox` — masks rendered field text and narration, and blocks copy/cut while hidden without changing editing.
- `ManageServerScreen` — adds the per-screen eye toggle to Add Server and Edit Server fields.
- `DirectJoinServerScreen` — adds the per-screen eye toggle to Direct Connection.
- `JoinMultiplayerScreen` — adds a keyboard-accessible eye toggle for server-list name masking.
- `ServerSelectionList.OnlineServerEntry` — masks matching address text, narration, and hover tooltip content.
- `ServerSelectionList.NetworkServerEntry` — masks the address shown for LAN entries.
- `ConnectScreen` — remembers the active display address so later failure messages can be sanitized without touching the connection.
- `DisconnectedScreen` — masks matching address text in the error reason and its narration.
- `Screen` — exposes the inherited widget-registration method through a minimal invoker for the eye controls.

## Logo and project links

The original logo source is preserved in `branding/`. The 512×512 Modrinth-ready icon is `icon-512.png`; the Fabric and Mod Menu icon is `src/main/resources/assets/ipmask/icon.png`.

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
