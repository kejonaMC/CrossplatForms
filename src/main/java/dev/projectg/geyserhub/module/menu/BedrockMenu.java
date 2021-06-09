package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.module.scoreboard.Placeholders;
import dev.projectg.geyserhub.utils.bstats.Reloadable;
import dev.projectg.geyserhub.utils.bstats.ReloadableRegistry;
import dev.projectg.geyserhub.utils.bstats.SelectorLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BedrockMenu extends Placeholders implements Reloadable {

    private static BedrockMenu instance;

    private boolean isEnabled = false;

    private SimpleForm serverSelector;
    private List<String> validServerNames;
    private List<List<String>> validCommands;
    private int commandsIndex;
    Player player;


    /**
     *
     * @return Get the latest BedrockMenu instance that was created
     */
    public static BedrockMenu getInstance() {
        return instance;
    }

    /**
     * Create a new bedrock selector form and initializes it with the current loaded config
     */
    public BedrockMenu() {
        instance = this;
        ReloadableRegistry.registerReloadable(this);
        reload();
    }
    @Override
    public boolean reload() {
        if (GeyserHubMain.getInstance().getConfig().getBoolean("Bedrock-Selector.Enable", true)) {
            if (load(GeyserHubMain.getInstance().getConfig())) {
                isEnabled = true;
            } else {
                isEnabled = false;
                return false;
            }
        }
        return true;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Initialize or refresh the server selector form
     */
    private boolean load(@Nonnull FileConfiguration config) {

        SelectorLogger logger = SelectorLogger.getLogger();

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
        String title = config.getString("Bedrock-Selector.Title");
        String content = config.getString("Bedrock-Selector.Content");
        if (title == null || content == null) {
            logger.severe("Value of Bedrock-Selector.Title or Bedrock-Selector.Content has no value in the config! Failed to create the bedrock selector form.");
            return false;
        }

        serverSelector = SimpleForm.of(title, content, allButtons);
        return true;
    }

    /**
     *  Get the server buttons and set {@link BedrockMenu#validServerNames}
     * @param logger The logger to send messages to
     * @param config The configuration to pull the servers from
     * @return A list of ButtonComponents, which may be empty.
     */
    private List<ButtonComponent> getServerButtons(@Nonnull SelectorLogger logger, @Nonnull FileConfiguration config) {
        // Enter the Bedrock-Selector.Servers section
        ConfigurationSection serverSection;
        if (config.contains("Bedrock-Selector.Servers", true)) {
            serverSection = config.getConfigurationSection("Bedrock-Selector.Servers");
            assert serverSection != null;
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
            if (serverInfo == null) {
                // This will be null if the serverName key isn't actually a configuration section
                logger.warn("Server entry with name \"" + serverName + "\" was not added to the bedrock selector because it was not formatted correctly!");
                continue;
            }

            if (serverInfo.contains("Button-Text", true) && serverInfo.isString("Button-Text")) {
                String withplaceholders = serverInfo.getString("Button-Text");
                assert withplaceholders != null;
                String buttonText = PlaceholderAPI.setPlaceholders(player.getPlayer(), withplaceholders);
                if (serverInfo.contains("ImageURL", true)) {
                    String imageURL = serverInfo.getString("ImageURL");
                    assert imageURL != null;
                    buttonComponents.add(ButtonComponent.of(buttonText, FormImage.Type.URL, imageURL));
                    logger.debug(serverName + " contains image");
                } else {
                    buttonComponents.add(ButtonComponent.of(buttonText));
                    logger.debug(serverName + " does not contain image");
                }
                validServerNames.add(serverName);
                logger.debug("added server for \"" + serverName + "\" with button text: " + buttonText);
            }
        }
        if (buttonComponents.isEmpty()) {
            logger.warn("Failed to create any valid server buttons for the form! The form configuration is malformed!");
        }

        // Save the valid server names so that the response handler knows the server identity of each button
        this.validServerNames = validServerNames;
        return buttonComponents;
    }

    /**
     * Get the server buttons and set {@link BedrockMenu#validCommands}
     * @param logger The logger to send messages to
     * @param config The configuration to pull the commands from
     * @return A list of ButtonComponents, which may be empty.
     */
    private List<ButtonComponent> getCommandButtons(@Nonnull SelectorLogger logger, @Nonnull FileConfiguration config) {

        // Enter the Bedrock-Selector.Commands section
        ConfigurationSection commandSection;
        if (config.contains("Bedrock-Selector.Commands", true)) {
            commandSection = config.getConfigurationSection("Bedrock-Selector.Commands");
            assert commandSection != null;
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
        List<List<String>> validCommands = new ArrayList<>();
        for (String commandEntry : allCommands) {
            ConfigurationSection commandInfo = commandSection.getConfigurationSection(commandEntry);
            if (commandInfo == null) {
                // This will be null if the serverName key isn't actually a configuration section
                logger.warn("Command entry with name \"" + commandEntry + "\" was not added to the bedrock selector because it was not formatted correctly!");
                continue;
            }

            if (commandInfo.contains("Button-Text", true) && commandInfo.isString("Button-Text") && commandInfo.contains("Commands", true) && commandInfo.isList("Commands")) {
                String buttonText = commandInfo.getString("Button-Text");
                assert buttonText != null;
                if (commandInfo.contains("ImageURL", true)) {
                    String imageURL = commandInfo.getString("ImageURL");
                    assert imageURL != null;
                    buttonComponents.add(ButtonComponent.of(buttonText, FormImage.Type.URL, imageURL));
                    logger.debug("Command " + commandEntry + " contains an image");
                } else {
                    buttonComponents.add(ButtonComponent.of(buttonText));
                    logger.debug("Command " + commandEntry + " does not contain an image");
                }
                validCommands.add(commandInfo.getStringList("Commands"));
                logger.debug("added command for \"" + commandEntry + "\" with button text: " + buttonText);
            }
        }
        if (buttonComponents.isEmpty()) {
            logger.warn("Failed to create any valid commands buttons for the form! The form configuration is malformed!");
        }

        // Save the valid commands so that the response handler knows which command should be sent for each button
        this.validCommands = validCommands;
        return buttonComponents;
    }

    /**
     * Send the server selector
     * @param floodgatePlayer the floodgate player to send it to
     */
    public void sendForm(@Nonnull FloodgatePlayer floodgatePlayer) {
        SelectorLogger logger = SelectorLogger.getLogger();

        Player player = Bukkit.getServer().getPlayer(floodgatePlayer.getCorrectUniqueId());
        if (player == null) {
            logger.severe("Unable to find a Bukkit Player for the given Floodgate Player: " + floodgatePlayer.getCorrectUniqueId().toString());
            return;
        }

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
                    player.sendPluginMessage(GeyserHubMain.getInstance(), "BungeeCord", b.toByteArray());
                    player.sendMessage(ChatColor.DARK_AQUA + "Trying to send you to: " + ChatColor.GREEN + serverName);
                } catch (IOException e) {
                    logger.severe("Failed to send a plugin message to Bungeecord!");
                    e.printStackTrace();
                }
            } else {
                // Get the commands from the list of commands and replace any playerName placeholders
                for (String command : validCommands.get(buttonID - commandsIndex)) {
                    String functionalCommand = command.replace("{playerName}", player.getName()).replace("{playerUUID}", player.getUniqueId().toString());
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), functionalCommand);
                }
            }
        });

        // Send the form to the floodgate player
        floodgatePlayer.sendForm(serverSelector);
    }
}