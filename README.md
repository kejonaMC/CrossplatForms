<img alt="CrossplatForms" width="500" src="https://github.com/ProjectG-Plugins/CrossplatForms/blob/main/images/crossplatForms.svg" /> 

[![Discord](https://img.shields.io/discord/853331530004299807?color=7289da&label=discord&logo=discord&logoColor=white)](https://discord.gg/M2SvqCu4e9)
[![License](https://img.shields.io/badge/License-GPL-orange)](https://github.com/ProjectG-Plugins/CrossplatForms/blob/master/LICENSE)
[![Build Status](https://ci.projectg.dev/job/CrossplatForms/job/main/badge/icon)](https://ci.projectg.dev/job/CrossplatForms/job/main/)
[![Version](https://img.shields.io/badge/version-0.3.0-blue)](https://github.com/ProjectG-Plugins/CrossplatForms/releases)

CrossplatForms is a Java Edition plugin aimed at creating Bedrock Edition Forms for [Geyser](https://github.com/GeyserMC/Geyser) players through flexible configurations. Bedrock Forms allow servers to provide players with abritrary choices in a styled menu, which the server then handles in any way desired. This allows you to customize forms specifically for your server, plugins, etc.

Inventory menus for Java Edition players can also be created. Forms and menus are together referred to as interfaces.

## Features:

* Define Actions to run commands, open interfaces, change servers, etc
* Simple, Modal, and Custom Forms for Bedrock Edition players
  * Simple and Modal Forms: Trigger different Actions depending on the button pressed
  * Custom Forms: Actions are trigggerd, and the response of each component is available as a placeholder
* Inventory menus. Trigger different Actions depending on the button pressed
* Access Items for players that trigger Actions when clicked in hand
* Register new commands that trigger Actions
* Trigger Actions when existing commands are executed
* Broad placeholder support

## Supported Platforms:

* [`CrossplatForms-Spigot.jar`](https://ci.projectg.dev/job/CrossplatForms/job/main/) :&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp; Spigot 1.14.4 - 1.18.2
* [`CrossplatForms-SpigotLegacy.jar`](https://ci.projectg.dev/job/CrossplatForms/job/main/) :&ensp; Spigot 1.8.8 - 1.13.2
* [`CrossplatForms-BungeeCord.jar`](https://ci.projectg.dev/job/CrossplatForms/job/main/) :&ensp;&ensp;&ensp; BungeeCord
* [`CrossplatForms-Velocity.jar`](https://ci.projectg.dev/job/CrossplatForms/job/main/) :&ensp;&ensp;&ensp;&ensp;&ensp; Velocity 3.x

BungeeCord and Velocity do not support Access Items. BungeeCord and Velocity don't support inventory menus yet, but they will in the future.

## Requirements:
* Java 8 or higher
* Geyser or Floodgate are required for Bedrock Edition specific features. Other features will still work without them.
  * If you want to install CForms on Spigot servers behind a BungeeCord/Velocity proxy, Floodgate must also be installed on the backend servers. Follow this [guide](https://wiki.geysermc.org/floodgate/setup/) closely. If you need setup help or Bedrock players are being treated as Java players, ask the [Geyser Discord](https://discord.gg/geysermc) for help.

### How to use Geyser instead of Floodgate:

Although using Floodgate should always be preferred, it is not required. If you want to use Geyser, [Java 16](https://adoptium.net/) or higher is required. There may be additional steps depending on your platform:

Paper 1.16.5 and newer, Velocity:&ensp;&ensp;&ensp; No extra steps necessary  
Spigot, BungeeCord, Waterfall:&ensp;&ensp;&ensp;&ensp;&ensp; Add `-Djdk.util.jar.enableMultiRelease=true` to your JVM startup flags.
