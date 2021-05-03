package com.alysaa.serverselector.form;

import com.alysaa.serverselector.GServerSelector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class SelectorForm {
    public static void SelectServer(Player player) {
        Logger logger = GServerSelector.getInstance().getLogger();
        FileConfiguration config = GServerSelector.getInstance().getConfig();
        UUID uuid = player.getUniqueId();
        boolean isFloodgatePlayer = FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (!isFloodgatePlayer) {
            player.sendMessage("Sorry, this is a Bedrock command!");
            return;
        }
        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(uuid);


        // todo: doing config validation outside of this would be much faster
        // todo: generate the button list (or even the whole form) on config load instead
        // todo: image caching?

        // Get the Form.Servers section
        ConfigurationSection serverSection;
        if (config.contains("Form.Servers")) {
            serverSection = config.getConfigurationSection("Form.Servers");
        } else {
            String badConfigMsg = "Failed to send a form because the configuration is malformed! Regenerate it.";
            player.sendMessage(badConfigMsg);
            logger.severe(badConfigMsg);
            return;
        }
        // Get all the defined servers
        Set<String> allServers = serverSection.getKeys(false);
        if (allServers.isEmpty()) {
            player.sendMessage("There are no defined servers for you to connect to!");
            logger.severe("Failed to send a form because there are no defined servers in the form configuration!");
            return;
        }
        // Create a list of buttons. For every server with a valid button configuration, we add its button. The index value is the button id.
        List<ButtonComponent> buttonComponents = new ArrayList<>();
        // This list will contain the name of every server that has a valid button. The index value is the button id.
        List<String> validServerNames = new ArrayList<>(2);
        for (String serverName : allServers) {
            ConfigurationSection serverInfo = serverSection.getConfigurationSection(serverName);
            if (serverInfo.contains("ButtonText")) {
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
            player.sendMessage("There are no valid servers to be displayed!");
            logger.warning("Failed to create any valid buttons for the server selector form! The form configuration is malformed.");
            return;
        }

        // Create the form without the Builder so that we have more control over the list of buttons
        SimpleForm serverSelector = SimpleForm.of(
                config.getString("Form.Title"),
                config.getString("Form.Content"),
                buttonComponents);

        // Set the response handler
        serverSelector.setResponseHandler((responseData) -> {
            SimpleFormResponse response = serverSelector.parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }
            // This should never be empty considering its length is the number of valid buttons
            String serverName = validServerNames.get(response.getClickedButtonId());
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(serverName);
            } catch (IOException eee) {
                Bukkit.getLogger().info("You'll never see me!");
            }
            player.sendPluginMessage(GServerSelector.getInstance(), "BungeeCord", b.toByteArray());
        });

        // Send the form to the floodgate player
        floodgatePlayer.sendForm(serverSelector);
    }
}