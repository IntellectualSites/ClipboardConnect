# Clipboard Connect

## Description
Clipboard Connect is a dynamic plugin for Minecraft, enabling the synchronization of WorldEdit clipboards across multiple servers using Redis. This tool streamlines the process of transferring builds and structures between servers, facilitating collaboration and efficient project management in Minecraft communities.

## Requirements
- Paper server version 1.20 or higher.
- WorldEdit plugin installed on the server.
- Docker and Docker Compose installation

## Motivation
The development of Clipboard Connect was sparked by two key challenges: the need within our network to efficiently transfer builds from one server to another, and a collaboration with TheBakery, who were seeking a similar solution. Clipboard Connect was created to meet these requirements, offering a streamlined and effective method for cross-server build transfers.

## Focus
The primary focus of Clipboard Connect is the synchronization of WorldEdit clipboards between Minecraft servers. It is designed for stability and reliability, ensuring seamless and efficient clipboard transfers, which are integral to enhancing the gameplay and creative experience on Minecraft servers.

## More Information / External Links
- Hangar: [https://hangar.papermc.io/OneLiteFeather/ClipboardConnect](https://hangar.papermc.io/OneLiteFeather/ClipboardConnect)
- Modrinth: [https://modrinth.com/plugin/clipboard-connect](https://modrinth.com/plugin/clipboard-connect)
- Discord: [https://discord.onelitefeather.net](https://discord.onelitefeather.net)

## Permissions

| Permission Pack            | Permission                       | Description |
|----------------------------|----------------------------------|-------------|
| `clipboardconnect.pack.basic` | `clipboardconnect.command.save`  | Save clipboard to Redis |
| `clipboardconnect.pack.basic` | `clipboardconnect.command.load`  | Load clipboard from Redis |
| `clipboardconnect.pack.basic` | `clipboardconnect.service.save`  | Automatic saving of clipboard when leaving the server |
| `clipboardconnect.pack.basic` | `clipboardconnect.service.load`  | Automatic loading of clipboard when joining the server |
| `clipboardconnect.pack.admin` | `clipboardconnect.command.setup` | Access to the setup command for configuring the plugin |
| `clipboardconnect.pack.admin` | -                                | Includes all `clipboardconnect.pack.basic` permissions |

## Commands
- `/clipboardconnect setup` - Starts an interactive setup process that guides the user through configuring the plugin.
- `/clipboardconnect save` - Saves the current clipboard to the Redis database.
- `/clipboardconnect load` - Loads a clipboard from the Redis database.

## Configuration
The configuration of Clipboard Connect is managed through the `/clipboardconnect setup` command. This in-game interactive process simplifies the configuration, focusing on essential settings:

- **Duration for TTL (Time To Live) of Clipboards**: Configures the duration for which clipboards are stored in Redis. The default is set to 6 hours.
- **Server Name for Sync Notification**: Specifies the server name used for sending sync push notifications.

These settings ensure that Clipboard Connect operates with optimal parameters for clipboard synchronization across servers.
