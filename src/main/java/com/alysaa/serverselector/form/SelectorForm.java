package com.alysaa.serverselector.form;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.utils.CheckJavaOrFloodPlayer;
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
        Logger logger = GServerSelector.plugin.getLogger();
        FileConfiguration config = GServerSelector.plugin.getConfig();
        UUID uuid = player.getUniqueId();
        boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
        if (!isFloodgatePlayer) {
            player.sendMessage("Sorry, this is a Bedrock command!");
            return;
        }
        FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(uuid);


        // todo: doing config validation outside of this would be much faster
        // todo: image caching?
        // todo: it may be nicer to have the server name instead of the button text as the config key, however currently its easier to calculate which server they pressed on.

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
        // Get all the defined servers by their button name
        Set<String> serverButtonNames = serverSection.getKeys(false);
        if (serverButtonNames.isEmpty()) {
            player.sendMessage("There are no defined servers for you to connect to!");
            logger.severe("Failed to send a form because there are no defined servers in the form configuration!");
            return;
        }
        // Create a list of buttons from all defined servers
        List<ButtonComponent> buttonComponents = new ArrayList<>();
        for (String buttonName : serverButtonNames) {
            ConfigurationSection serverInfo = serverSection.getConfigurationSection(buttonName);
            // We don't use ServerName immediately but it must be there for later
            if (serverInfo.contains("ServerName")) {
                if (serverInfo.contains("ImageURL")) {
                    buttonComponents.add(ButtonComponent.of(buttonName, FormImage.Type.URL, serverInfo.getString("ImageURL")));
                } else {
                    buttonComponents.add(ButtonComponent.of(buttonName));
                }
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
            // This should not return null considering we do checks in the form constructions
            String serverName = config.getString("Form.Servers." + response.getClickedButton().getText() + ".ServerName");
            if (serverName != null) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Connect");
                    out.writeUTF(serverName);
                } catch (IOException eee) {
                    Bukkit.getLogger().info("You'll never see me!");
                }
                player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", b.toByteArray());
            } else {
                String noServer = "Failed to find a ServerName value in the config for '" + response.getClickedButton().getText() + "'";
                player.sendMessage(noServer);
                GServerSelector.plugin.getLogger().warning(noServer);
            }
        });

        // Send the form to the floodgate player
        fPlayer.sendForm(serverSelector);
    }
}