package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.Reloadable;
import dev.projectg.geyserhub.ReloadableRegistry;
import dev.projectg.geyserhub.SelectorLogger;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BedrockMenu implements Reloadable {

    private static BedrockMenu instance;

    private boolean isEnabled = false;

    private String title;
    private String content;
    private List<ButtonComponent> allButtons;

    /**
     * List of all the BungeeCord servers for all the buttons.
     */
    private List<String> serverNames;
    /**
     * List containing a list of commands for every button
     */
    private List<List<String>> commands;

    /**
     * Index at which command buttons start in {@link #allButtons}
     */
    int commandsIndex;

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

        String title = config.getString("Bedrock-Selector.Title");
        String content = config.getString("Bedrock-Selector.Content");
        if (title == null || content == null) {
            logger.severe("Value of Bedrock-Selector.Title or Bedrock-Selector.Content has no value in the config! Failed to create the bedrock selector form.");
            return false;
        }

        Map<ButtonComponent, String> serverButtonMap = getServerButtons(config);
        Map<ButtonComponent, List<String>> commandButtonMap = getCommandButtons(logger, config);
        if (serverButtonMap.isEmpty() && commandButtonMap.isEmpty()) {
            logger.severe("Failed to create any valid buttons for the form! The form configuration is malformed!");
            return false;
        }

        // Only set everything once it has been validated
        this.title = title;
        this.content = content;
        List<ButtonComponent> allButtons = new ArrayList<>();
        allButtons.addAll(serverButtonMap.keySet());
        allButtons.addAll(commandButtonMap.keySet());
        this.allButtons = allButtons;
        this.serverNames = new ArrayList<>(serverButtonMap.values());
        this.commands = new ArrayList<>(commandButtonMap.values());
        this.commandsIndex = serverButtonMap.size();

        return true;
    }

    /**
     *  Get the server buttons and each button's server
     * @param config The configuration to pull the servers from
     * @return A list of ButtonComponents, which may be empty.
     */
    private LinkedHashMap<ButtonComponent, String> getServerButtons(@Nonnull FileConfiguration config) {
        SelectorLogger logger = SelectorLogger.getLogger();

        // Enter the Bedrock-Selector.Servers section
        ConfigurationSection serverSection;
        if (config.contains("Bedrock-Selector.Servers", true)) {
            serverSection = config.getConfigurationSection("Bedrock-Selector.Servers");
            assert serverSection != null;
        } else {
            logger.debug("Failed to create any server buttons because the configuration is malformed! Regenerate it.");
            return new LinkedHashMap<>(Collections.emptyMap());
        }
        // Get all the defined servers in our config
        Set<String> allServers = serverSection.getKeys(false);
        if (allServers.isEmpty()) {
            logger.debug("Failed to create any server buttons because there are no defined servers in the form configuration!");
            return new LinkedHashMap<>(Collections.emptyMap());
        }
        // Create a map of buttons and their server. For every defined server with a valid button configuration, we add its button.
        LinkedHashMap<ButtonComponent, String> buttonComponents = new LinkedHashMap<>();
        for (String serverName : allServers) {
            ConfigurationSection serverInfo = serverSection.getConfigurationSection(serverName);
            if (serverInfo == null) {
                // This will be null if the serverName key isn't actually a configuration section
                logger.warn("Server entry with name \"" + serverName + "\" was not added to the bedrock selector because it was not formatted correctly!");
                continue;
            }

            if (serverInfo.contains("Button-Text", true) && serverInfo.isString("Button-Text")) {
                String buttonText = serverInfo.getString("Button-Text");
                assert buttonText != null;
                if (serverInfo.contains("ImageURL", true)) {
                    String imageURL = serverInfo.getString("ImageURL");
                    assert imageURL != null;
                    buttonComponents.put(ButtonComponent.of(buttonText, FormImage.Type.URL, imageURL), serverName);
                    logger.debug(serverName + " contains image");
                } else {
                    buttonComponents.put(ButtonComponent.of(buttonText), serverName);
                    logger.debug(serverName + " does not contain image");
                }
                logger.debug("added server for \"" + serverName + "\" with button text: " + buttonText);
            }
        }
        if (buttonComponents.isEmpty()) {
            logger.warn("Failed to create any valid server buttons for the form! The form configuration is malformed!");
        }

        return buttonComponents;
    }

    /**
     * Get the command buttons and their commands
     * @param logger The logger to send messages to
     * @param config The configuration to pull the commands from
     * @return A list of ButtonComponents, which may be empty.
     */
    private LinkedHashMap<ButtonComponent, List<String>> getCommandButtons(@Nonnull SelectorLogger logger, @Nonnull FileConfiguration config) {

        // Enter the Bedrock-Selector.Commands section
        ConfigurationSection commandSection;
        if (config.contains("Bedrock-Selector.Commands", true)) {
            commandSection = config.getConfigurationSection("Bedrock-Selector.Commands");
            assert commandSection != null;
        } else {
            logger.debug("Failed to create any command buttons because the configuration is malformed! Regenerate it.");
            return new LinkedHashMap<>(Collections.emptyMap());
        }
        // Get all the defined commands in our config
        Set<String> allCommands = commandSection.getKeys(false);
        if (allCommands.isEmpty()) {
            logger.debug("Failed to create any command buttons because there are no defined commands in the form configuration!");
            return new LinkedHashMap<>(Collections.emptyMap());
        }
        // Create a map of buttons and their commands. For every defined command with a valid configuration, we add its button.
        LinkedHashMap<ButtonComponent, List<String>> buttonComponents = new LinkedHashMap<>();
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
                    buttonComponents.put(ButtonComponent.of(buttonText, FormImage.Type.URL, imageURL), commandInfo.getStringList("Commands"));
                    logger.debug("Command " + commandEntry + " contains an image");
                } else {
                    buttonComponents.put(ButtonComponent.of(buttonText), commandInfo.getStringList("Commands"));
                    logger.debug("Command " + commandEntry + " does not contain an image");
                }
                logger.debug("added command for \"" + commandEntry + "\" with button text: " + buttonText);
            }
        }
        // Warn if there were defined commands but they were all malformed
        if (buttonComponents.isEmpty()) {
            logger.warn("Failed to create any valid commands buttons for the form! The form configuration is malformed!");
        }

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

        // Resolve any placeholders in the button text
        List<ButtonComponent> formattedButtons = new ArrayList<>();
        for (ButtonComponent component : allButtons) {
            formattedButtons.add(ButtonComponent.of(PlaceholderAPI.setPlaceholders(player, component.getText()), component.getImage()));
        }
        // Create the form
        SimpleForm serverSelector = SimpleForm.of(title, content, formattedButtons);

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
                String serverName = serverNames.get(buttonID);
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
                for (String command : commands.get(buttonID - commandsIndex)) {
                    String functionalCommand = command.replace("{playerName}", player.getName()).replace("{playerUUID}", player.getUniqueId().toString());
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), PlaceholderAPI.setPlaceholders(player, functionalCommand));
                }
            }
        });

        // Send the form to the floodgate player
        floodgatePlayer.sendForm(serverSelector);
    }
}