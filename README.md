# GeyserServerSelector

Switching servers with a form for bedrock players!


## Installation

GeyserServerSelector is an lobby plugin that need to be installed on the Lobby server of a bungeecord network!
You will need to have the floodgateapi send to the backend lobby server see; https://github.com/GeyserMC/Geyser/wiki/Floodgate#running-floodgate-on-spigot-servers-behind-bungeecord-or-velocity


## Permissions:
```
gserverselector.servers
```


## Commands:
This command will only work for bedrock players!
```
/servers 
```


## EnableSelector:

When set on false the plugin will net check if player is floodgate or java and will be disabled.


## ItemJoin:

If enabled this will give the bedrock player an compass, when clicked it will open the selector form!


## Slot:

The Default slot is 4. when player join and gets compass it will spawn in slot number 4.


## DisableItemDrop:

Default this option is enabled. when enabled players cannot drop items from their inventory


## DisableItemMove:

Default this option is enabled. when enabled players cannot move items in their inventory!


## Form:

Title: you can set the Title of a form.
Content: you can add a discription of the form or server info.


## Button Titles

Here you can edit the text inside the buttons.

## Define Bungeecord ServerNames
```
ServerName1: Lobby
```
You need to define the real servername used in the bungeecord config.yml!


## Button Immages Url's

Each button got its own image. you will need to set the url path to the image with an extention of png!
you can check the config for example!


## Creator 
Jens


