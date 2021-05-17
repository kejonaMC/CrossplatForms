package com.alysaa.serverselector.form;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.SelectorLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class SelectorForm {

    private static SimpleForm serverSelector;

    private static List<String> validServerNames;

    private static List<String> validCommands;
    private static int commandsIndex;

    private static final ItemStack formItem;
    static {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Server Selector"));
        compass.setItemMeta(compassMeta);
        formItem = compass;
    }


    /**
     * Initialize or refresh the server selector form
     */
    public static boolean init() {
        GServerSelector.getInstance().loadConfig();

        SelectorLogger logger = SelectorLogger.getLogger();
        FileConfiguration config = GServerSelector.getInstance().getConfig();

        List<ButtonComponent> allButtons= new ArrayList<>();
        allButtons.addAll(getServerButtons(logger, config));
        allButtons.addAll(getCommandButtons(logger, config));
        if (allButtons.isEmpty()) {
            logger.severe("Failed to create any valid buttons for the form! The form configuration is malformed!");
            return false;
        }

        // The start index of the command buttons is simply the amount of server buttons
        commandsIndex = validServerNames.size();

        // Create the form without the Builder so that we have more control over the list of buttons
        SelectorForm.serverSelector = SimpleForm.of(
                config.getString("Form.Title"),
                config.getString("Form.Content"),
                allButtons);
        return true;
    }

    /**
     *  Get the server buttons and set {@link SelectorForm#validServerNames}
     * @param logger The logger to send messages to
     * @param config The configuration to pull the servers from
     * @return A list of ButtonComponents, which may be empty.
     */
    private static List<ButtonComponent> getServerButtons(SelectorLogger logger, FileConfiguration config) {

        // Enter the Form.Servers section
        ConfigurationSection serverSection;
        if (config.contains("Form.Servers")) {
            serverSection = config.getConfigurationSection("Form.Servers");
        } else {
            logger.debug("Failed to create any server buttons because the configuration is malformed! Regenerate it.");
            return Collections.emptyList();
        }
        // Get all the defined servers in our config
        Set<String> allServers = serverSection.getKeys(false);
        if (allServers.isEmpty()) {
            logger.debug("Failed to create any server buttons because there are no defined servers in the form configuration!");
            return Collections.emptyList();
        }
        // Create a list of buttons. For every defined server with a valid button configuration, we add its button. The index value is the button id.
        List<ButtonComponent> buttonComponents = new ArrayList<>();
        // This list will contain the name of every server that has a valid button. The index value is the button id.
        List<String> validServerNames = new ArrayList<>(2);
        for (String serverName : allServers) {
            ConfigurationSection serverInfo = serverSection.getConfigurationSection(serverName);
            if (serverInfo.contains("ButtonText") && serverInfo.isString("ButtonText")) {
                String buttonText = serverInfo.getString("ButtonText");
                if (serverInfo.contains("ImageURL")) {
                    buttonComponents.add(ButtonComponent.of(buttonText, FormImage.Type.URL, serverInfo.getString("ImageURL")));
                } else {
                    buttonComponents.add(ButtonComponent.of(buttonText));
                }
                validServerNames.add(serverName);
            }
        }
        if (buttonComponents.isEmpty()) {
            logger.debug("Failed to create any valid server buttons for the form! The form configuration is malformed!");
        }

        // Save the valid server names so that the response handler knows the server identity of each button
        SelectorForm.validServerNames = validServerNames;
        return buttonComponents;
    }

    /**
     * Get the server buttons and set {@link SelectorForm#validCommands}
     * @param logger The logger to send messages to
     * @param config The configuration to pull the commands from
     * @return A list of ButtonComponents, which may be empty.
     */
    private static List<ButtonComponent> getCommandButtons(SelectorLogger logger, FileConfiguration config) {

        // Enter the Form.Commands section
        ConfigurationSection commandSection;
        if (config.contains("Form.Commands")) {
            commandSection = config.getConfigurationSection("Form.Commands");
        } else {
            logger.debug("Failed to create any command buttons because the configuration is malformed! Regenerate it.");
            return Collections.emptyList();
        }
        // Get all the defined servers in our config
        Set<String> allCommands = commandSection.getKeys(false);
        if (allCommands.isEmpty()) {
            logger.debug("Failed to create any command buttons because there are no defined commands in the form configuration!");
            return Collections.emptyList();
        }
        // Create a list of buttons. For every defined command with a valid configuration, we add its button. The index value is the button id.
        List<ButtonComponent> buttonComponents = new ArrayList<>();
        // This list will contain every command that has a valid button. The index value is the button id.
        List<String> validCommands = new ArrayList<>();
        for (String commandEntry : allCommands) {
            ConfigurationSection commandInfo = commandSection.getConfigurationSection(commandEntry);
            if (commandInfo.contains("ButtonText", true) && commandInfo.isString("ButtonText") && commandInfo.contains("Command", true) && commandInfo.isString("Command")) {
                String buttonText = commandInfo.getString("ButtonText");
                if (commandInfo.contains("ImageURL")) {
                    buttonComponents.add(ButtonComponent.of(buttonText, FormImage.Type.URL, commandInfo.getString("ImageURL")));
                } else {
                    buttonComponents.add(ButtonComponent.of(buttonText));
                }
                validCommands.add(commandInfo.getString("Command"));
            }
        }
        if (buttonComponents.isEmpty()) {
            logger.debug("Failed to create any valid commands buttons for the form! The form configuration is malformed!");
        }

        // Save the valid commands so that the response handler knows which command should be sent for each button
        SelectorForm.validCommands = validCommands;
        return buttonComponents;
    }

    /**
     * Send the server selector
     * @param player the floodgate player to send it to
     */
    public static void sendForm(Player player) {
        UUID uuid = player.getUniqueId();
        boolean isFloodgatePlayer = FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (!isFloodgatePlayer) {
            player.sendMessage("Sorry, this is a Bedrock command!");
            return;
        }
        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(uuid);

        // Set the response handler
        serverSelector.setResponseHandler((responseData) -> {
            SimpleFormResponse response = serverSelector.parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            int buttonID = response.getClickedButtonId();
            if (buttonID < commandsIndex) {
                // This should never be out of bounds considering its size is the number of valid buttons
                String serverName = validServerNames.get(buttonID);
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Connect");
                    out.writeUTF(serverName);
                    player.sendPluginMessage(GServerSelector.getInstance(), "BungeeCord", b.toByteArray());
                } catch (IOException e) {
                    SelectorLogger.getLogger().severe("Failed to send a plugin message to Bungeecord!");
                    e.printStackTrace();
                }
            } else {
                // Get the command from the list of commands and replace any playerName placeholders
                String functionalCommand = validCommands.get(buttonID - commandsIndex).replace("{playerName}", player.getName());
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), functionalCommand);
            }
        });

        // Send the form to the floodgate player
        floodgatePlayer.sendForm(serverSelector);
    }

    public static ItemStack getItem() {
        return formItem;
    }
}