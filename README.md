[![Discord](https://img.shields.io/discord/806179549498966058?color=7289da&label=discord&logo=discord&logoColor=white)](https://discord.gg/xXzzdAXa2b)


# GeyserServerSelector

Allow Floodgate players to switch BungeeCord servers using Bedrock forms.

###### Note: This is NOT an official GeyserMC plugin. It is made to work with Geyser, but it is not maintained or produced by GeyserMC. If you need support with this plugin, please do not ask the Geyser developers — instead, please go to our Discord server which is linked above.

## Installation

GeyserServerSelector is a lobby plugin that must be installed on any backend servers you want the selector on.
Floodgate 2.0 must be installed on the backend servers.
See this [guide](https://github.com/GeyserMC/Geyser/wiki/Floodgate#running-floodgate-on-spigot-servers-behind-bungeecord-or-velocity) for more information

## Commands:

| Command | Permission | Info |
| ------- | -----------| ---- |
| `gteleporter` | `gserverselector.teleporter` | Open the server selector. Only available to bedrock players. | 
| `gssreload` | `gserverselector.reload` | Reload the contents of the server selector form. |


## Configuration

#### `ItemJoin`:
*Default:* `true`  

> Gives the player a compass which they can use to open the selector form.


#### `Slot`:
*Default:* `4`

> The slot of the hotbar that the compass is placed in. If the slot is full, the compass will be placed in a different slot of the hotbar if available. 


#### `AllowItemDrop`:
*Default:* `false`

> If false, stops the player from dropping the compass. 

#### `DestroyDroppedItem`
*Default:* `true`

> Destroy the compass if the player drops it.

#### `AllowItemMove`:
*Default:* `false`

> If disabled, stops the player from moving the compass.

#### `EnableDebug`:
*Default:* `false`
> Enable debug logging to the console.


### Form section:
***

#### `Title`:
*Default:* `"Server Selector"`
> The name of the form.

#### `Content`:
*Default:* `"Click on the server button of choice"`
> A small description below the Title.

#### `Servers`:
*Default:* See [here](https://github.com/ProjectG-Plugins/GeyserServerSelector/blob/master/src/main/resources/config.yml) for proper formatting.
> Add as many servers as you want. The name of the server must be the exact server name defined in bungeecord's config.  
> `ButtonText` is the text that will be shown on the button.  
> `ImageURL` is a link to an image that will be displayed to the left of each button.
      
#### `Commands`:
*Default:* See [here](https://github.com/ProjectG-Plugins/GeyserServerSelector/blob/master/src/main/resources/config.yml) for proper formatting.
> Add as many command buttons as you want. The name for each button (`Extra One`, etc) has no significance, as long as they are all different.
> 
> `ButtonText` is the text that will be shown on the button.   
> `Commands` is a list of console commands that will be run once the button is pressed. `{playerName}` is a placeholder that can be used in the command which is replaced with the exact name of the player.
> `ImageURL` is a link to an image that will be displayed to the left of each button.

#### Note:
Either the `Servers` or the `Commands` section can be set empty without issue.  
Additionally, the `ImageURL` key of each button can be removed in order for no image to be displayed next to the button. 


## Creators
Jens & Konicai


