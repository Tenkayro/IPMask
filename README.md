<p align="center">
  <img src="branding/ipmask-icon-64.png" alt="IPMask logo" width="64" height="64">
</p>

<h1 align="center">IPMask</h1>

<p align="center">
  Keep server addresses out of your screenshots, recordings, and streams.
</p>

**IPMask** is a client-side privacy mod for Minecraft. It masks server addresses on screen while leaving your saved server details and connection behavior unchanged.

## Features

- **Private address fields:** Add Server, Edit Server, and Direct Connection addresses start hidden and appear as `*`.
- **Easy to use:** Address fields stay editable. A small eye button lets you reveal or hide an address for the current screen.
- **Safer while hidden:** Copying and cutting are blocked for hidden address fields; pasting still works.
- **In-game masking:** Optionally masks the connected server address in Minecraft-rendered text, including chat, signs, holograms, GUI text, and hover tooltips.
- **Server list privacy:** Masks addresses in LAN entries and server-list tooltips. Server names are intentionally unchanged.
- **Connection screens and narration:** Masks matching addresses on connection/disconnection screens and in supported narration.
- **Customizable:** Separate settings let you choose which areas are masked.

## Settings

The masking options are enabled by default. To customize them, install the optional [Mod Menu](https://modrinth.com/mod/modmenu) and [YACL](https://modrinth.com/mod/yacl), then open **Mods → IPMask → Config**.

You can toggle masking separately for:

- Address fields
- Server-list addresses
- In-game text, such as chat, signs, and holograms
- Screen-reader narration
- Connection and disconnection screens

Your choices are saved in `config/ipmask.properties`.

## Installation

1. Install **Fabric Loader** and **Fabric API** for Minecraft **26.3**.
2. Download the IPMask `.jar` and place it in your Minecraft `mods` folder.
3. Launch the game with your Fabric profile.
4. Optionally install Mod Menu and YACL to customize the masking settings.

IPMask is client-side: **servers do not need to install it**.

## Important notes

IPMask changes what Minecraft displays; it does not change or delete saved server data. It is not a VPN or proxy and does not hide your own IP address from servers. Server names are left as-is, so an address included in a server name may still be visible. Third-party mods with their own text-rendering paths may also display text outside IPMask's coverage.

## Support

Found a bug or have a suggestion? [Open an issue on GitHub](https://github.com/Tenkayro/IPMask/issues).
