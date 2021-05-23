package dev.projectg.geyserhub.bedrockmenu;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.Reloadable;
import dev.projectg.geyserhub.SelectorLogger;
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

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class BedrockMenu implements Reloadable {

    private static BedrockMenu instance;
    private static final ItemStack SELECTOR_ITEM;
    static {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        assert compassMeta != null;
        compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Server Selector"));
        compass.setItemMeta(compassMeta);
        SELECTOR_ITEM = compass;
    }

    private SimpleForm serverSelector;
    private List<String> validServerNames;
    private List<List<String>> validCommands;
    private int commandsIndex;


    /**
     *
     * @return Get the latest BedrockMenu instance that was created
     */
    public static BedrockMenu getInstance() {
        return instance;
    }

    /**
     * Create a new bedrock selector form and initializes it.
     * @param config the configuration to use for construction
     */
    public BedrockMenu(@Nonnull FileConfiguration config) {
        instance = this;
        load(config);
    }
    @Override
    public boolean reload() {
         return load(GeyserHubMain.getInstance().getConfig());
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
        String title = config.getString("Form.Title");
        String content = config.getString("Form.Content");
        if (title == null || content == null) {
            logger.severe("Value of Form.Title or Form.Content has no value in the config! Failed to create the bedrock selector form.");
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

        // Enter the Form.Servers section
        ConfigurationSection serverSection;
        if (config.contains("Form.Servers", true)) {
            serverSection = config.getConfigurationSection("Form.Servers");
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
                String buttonText = serverInfo.getString("Button-Text");
                assert buttonText != null;
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

        // Enter the Form.Commands section
        ConfigurationSection commandSection;
        if (config.contains("Form.Commands", true)) {
            commandSection = config.getConfigurationSection("Form.Commands");
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
     * @param player the floodgate player to send it to
     */
    public void sendForm(Player player) {
        SelectorLogger logger = SelectorLogger.getLogger();

        UUID uuid = player.getUniqueId();
        boolean isFloodgatePlayer = FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (!isFloodgatePlayer) {
            player.sendMessage("Sorry, this is a Bedrock command!");
            return;
        }
        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(uuid);

        if (serverSelector == null) {
            player.sendMessage("The form is broken! Please contact a server administrator");
            logger.warn("The bedrock server selector is null! Try reloading it.");
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
                } catch (IOException e) {
                    logger.warn("Failed to send a plugin message to Bungeecord!");
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

    public static ItemStack getItem() {
        return SELECTOR_ITEM;
    }
}