package dev.projectg.geyserhub.module.menu.bedrock;

import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.module.menu.MenuUtils;
import dev.projectg.geyserhub.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BedrockForm {

    private final SelectorLogger logger;

    /**
     * If the form actually works and can be used.
     */
    public final boolean isEnabled;

    /**
     * The name of the form, from the config.
     */
    public final String formName;

    /**
     * The title of the Bedrock form (shown in the GUI)
     */
    private String title;

    /**
     * The text shown under the {@link #title}
     */
    private String content;

    /**
     * A list of all the buttons. Index is the button ID.
     */
    private List<BedrockButton> allButtons;

    // todo: constructor that doesnt use config section

    /**
     * Create a new bedrock selector form and initializes it with the given form config section
     */
    protected BedrockForm(@Nonnull ConfigurationSection configSection) {
        logger = SelectorLogger.getLogger();
        Objects.requireNonNull(configSection);
        formName = configSection.getName();

        // Get the Title and Content
        if (!configSection.contains("Title") || !configSection.contains("Content")) {
            logger.warn("Bedrock Form: "  + formName + " does not contain a Title or Content value! Failed to create the form.");
            isEnabled = false;
            return;
        } else {
            this.title = Objects.requireNonNull(configSection.getString("Title"));
            this.content = Objects.requireNonNull(configSection.getString("Content"));
        }

        // Get our Buttons
        if (!(configSection.contains("Buttons", true) && configSection.isConfigurationSection("Buttons"))) {
            logger.warn("Bedrock Form: " + formName + " does not contain a Buttons section, unable to create form");
            isEnabled = false;
            return;
        }
        ConfigurationSection buttonSection = configSection.getConfigurationSection("Buttons");
        Objects.requireNonNull(buttonSection);
        List<BedrockButton> buttons = getButtons(buttonSection);
        if (buttons.isEmpty()) {
            logger.warn("Failed to create any valid buttons of Bedrock form: " + formName + "! All listed buttons have a malformed section!");
            isEnabled = false;
            return;
        } else {
            logger.debug("Finished adding buttons to bedrock form: " + formName);
        }
        this.allButtons = buttons;

        isEnabled = true;
    }

    /**
     *  Get all the buttons in the "Buttons" section
     * @return A list of Buttons, which may be empty.
     */
    private List<BedrockButton> getButtons(@Nonnull ConfigurationSection configSection) {
        logger.debug("Getting buttons for form: " + formName);

        // Get all the defined buttons in the buttons section
        Set<String> allButtonIds = configSection.getKeys(false);
        if (allButtonIds.isEmpty()) {
            logger.warn("No buttons were listed for form: " + formName);
            return Collections.emptyList();
        }

        // Create a list of buttons. For every defined button with a valid server or command configuration, we add its button.
        List<BedrockButton> compiledButtons = new LinkedList<>();
        for (String buttonId : allButtonIds) {
            ConfigurationSection buttonInfo = configSection.getConfigurationSection(buttonId);
            if (buttonInfo == null) {
                // This will be null if the buttonId key isn't actually a configuration section
                logger.warn(buttonId + " was not added because it is not a configuration section!");
                continue;
            }

            if (buttonInfo.contains("Button-Text", true) && buttonInfo.isString("Button-Text")) {
                String buttonText = buttonInfo.getString("Button-Text");
                Objects.requireNonNull(buttonText);
                logger.debug(buttonId + " has Button-Text: " + buttonText);

                // Add image if specified
                FormImage image = null;
                if (buttonInfo.contains("ImageURL", true)) {
                    String imageURL = buttonInfo.getString("ImageURL");
                    Objects.requireNonNull(imageURL);
                    image = FormImage.of(FormImage.Type.URL, imageURL);
                    logger.debug(buttonId + " contains image with URL: " + image.getData());
                }

                // Add commands if specified
                List<String> commands = Collections.emptyList();
                if (buttonInfo.contains("Commands") && buttonInfo.isList("Commands")) {
                    if (buttonInfo.getStringList("Commands").isEmpty()) {
                        logger.warn(buttonId + " contains commands list but the list was empty.");
                    } else {
                        commands = buttonInfo.getStringList("Commands");
                        logger.debug(buttonId + " contains commands: " + commands);
                    }
                }

                // Add server if specified
                String serverName = null;
                if (buttonInfo.contains("Server") && buttonInfo.isString("Server")) {
                    serverName = buttonInfo.getString("Server");
                    Objects.requireNonNull(serverName);
                    logger.debug(buttonId + " contains BungeeCord target server: " + serverName);
                }

                BedrockButton button = new BedrockButton(buttonText);
                button.setImage(image);
                button.setCommands(commands);
                button.setServer(serverName);
                compiledButtons.add(button);

                logger.debug(buttonId + " was successfully added.");
            } else {
                logger.warn(buttonId + " does not contain a valid Button-Text value, not adding.");
            }
        }

        return compiledButtons;
    }

    /**
     * Send the server selector
     * @param floodgatePlayer the floodgate player to send it to
     */
    public void sendForm(@Nonnull FloodgatePlayer floodgatePlayer) {
        if (!isEnabled) {
            throw new AssertionError("Form: " + title + " that failed to load was called to be sent to a player!");
        }

        SelectorLogger logger = SelectorLogger.getLogger();

        Player player = Bukkit.getServer().getPlayer(floodgatePlayer.getCorrectUniqueId());
        if (player == null) {
            logger.severe("Unable to find a Bukkit Player for the given Floodgate Player: " + floodgatePlayer.getCorrectUniqueId().toString());
            return;
        }

        // Resolve any placeholders in the button text
        List<BedrockButton> formattedButtons = new ArrayList<>();
        for (BedrockButton rawButton : allButtons) {
            BedrockButton copiedButton = new BedrockButton(rawButton);
            copiedButton.setText(PlaceholderUtils.setPlaceholders(player, copiedButton.getText()));
            formattedButtons.add(copiedButton);
        }

        // Create the form
        SimpleForm serverSelector = SimpleForm.of(PlaceholderUtils.setPlaceholders(player, title), PlaceholderUtils.setPlaceholders(player, content), formattedButtons.stream().map(BedrockButton::getButtonComponent).collect(Collectors.toList()));

        // Set the response handler
        serverSelector.setResponseHandler((responseData) -> {
            SimpleFormResponse response = serverSelector.parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            BedrockButton button = formattedButtons.get(response.getClickedButtonId());

            // Run the commands if given, move the player to another server if given.
            MenuUtils.affectPlayer(button.getCommands(), button.getServer(), player);
        });

        // Send the form to the floodgate player
        floodgatePlayer.sendForm(serverSelector);
    }
}
