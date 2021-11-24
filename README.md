[![Spigot page downloads](https://img.shields.io/spiget/downloads/95455?color=yellow&label=Spigot%20page%20downloads)](https://www.spigotmc.org/resources/geyserhub.95455/)
[![Discord](https://img.shields.io/discord/806179549498966058?color=7289da&label=discord&logo=discord&logoColor=white)](https://discord.gg/M2SvqCu4e9)
[![Build Status](https://ci.projectg.dev/job/GeyserHub/job/master/badge/icon)](https://ci.projectg.dev/job/GeyserHub/job/master/)
[![License](https://img.shields.io/badge/License-GPL-orange)](https://github.com/ProjectG-Plugins/GeyserUpdater/blob/master/LICENSE)


# GeyserHub

A crossplay lobby plugin for Spigot servers that provides server selector and command menus through Bedrock forms for Bedrock players, and inventory menus for Java players, with high customizability. Supports BungeeCord, and Velocity if BungeeCord plugin messaging is enabled in `velocity.toml`.

### Other Features:
Anything can be toggled off.
- Fully customizable Bedrock forms and Java inventory menus that can run commands and move players to other servers
- Hotbar items for quick access to different forms/menus
- Full Placeholder API support
- Simple scoreboard
- Welcome messages**
- Automatic broadcasts
- World restrictions

 The legacy simple ServerSelector only for Geyser players can be found [here](https://ci.projectg.dev/job/GeyserHub/job/legacy-selector/).
###### Note: This is NOT an official GeyserMC plugin. It is made to work with Geyser, but it is not maintained or produced by GeyserMC. If you need support with this plugin, please do not ask the Geyser developers — instead, please go to our Discord server which is linked above.

## Installation

GeyserHub is a lobby plugin that must be installed on any backend servers you want the selector on.
[Floodgate](https://github.com/GeyserMC/Floodgate) must be installed on the backend servers, AND properly configured. 
See this [guide](https://github.com/GeyserMC/Geyser/wiki/Floodgate#running-floodgate-on-spigot-servers-behind-bungeecord-or-velocity) for more information.

## Commands:

| Command | Permission | Info |
| ------- | -----------| ---- |
| `ghub` | `geyserhub.main` | Open the default server selector (The help page if console)| 
| `ghub form` | `geyserhub.form` | Open a form/menu (The help page if console)|
| `ghub <form> <player>` | `geyserhub.form.others` | Make someone else open a form/menu |
| `ghub reload` | `geyserhub.reload` | Reload the configuration |

## Permissions:

| Permission | Info |
| -----------| ---- |
| Command permissions listed above | Permissions that are for commands| 
| `geyserhub.blockbreak` | Bypass the world settings to break blocks| 
| `geyserhub.blockplace` | Bypass the world settings to place blocks|

## Configuration

See the documentation for our config [here](https://github.com/ProjectG-Plugins/GeyserHub/wiki/Configuration-Docs).

## Creators
Jens & Konicai

ProjectG


