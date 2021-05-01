package com.alysaa.serverselector.form;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.utils.CheckJavaOrFloodPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SelectorForm {
    public static void SelectServer(Player player) {
        FileConfiguration config = GServerSelector.plugin.getConfig();
        UUID uuid = player.getUniqueId();
        boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
        if (isFloodgatePlayer) {
            FloodgatePlayer fplayer = FloodgateApi.getInstance().getPlayer(uuid);
            fplayer.sendForm(
                    SimpleForm.builder()
                            .title(config.getString("Form.Title"))
                            .content(config.getString("Form.Content"))
                            .button(config.getString("Form.Button1"), FormImage.Type.URL, config.getString("Form.Url1"))
                            .button(config.getString("Form.Button2"), FormImage.Type.URL, config.getString("Form.Url2"))
                            .button(config.getString("Form.Button3"), FormImage.Type.URL, config.getString("Form.Url3"))
                            .button(config.getString("Form.Button4"), FormImage.Type.URL, config.getString("Form.Url4"))
                            .button(config.getString("Form.Button5"), FormImage.Type.URL, config.getString("Form.Url5"))
                            .button(config.getString("Form.Button6"), FormImage.Type.URL, config.getString("Form.Url6"))
                            .button(config.getString("Form.Button7"), FormImage.Type.URL, config.getString("Form.Url7"))
                            .button(config.getString("Form.Button8"), FormImage.Type.URL, config.getString("Form.Url8"))
                            .button(config.getString("Form.Button9"), FormImage.Type.URL, config.getString("Form.Url9"))
                            .button(config.getString("Form.Button10"), FormImage.Type.URL, config.getString("Form.Url10"))
                            .responseHandler((form, responseData) -> {
                                SimpleFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) {
                                    // isCorrect() = !isClosed() && !isInvalid()
                                    // player closed the form or returned invalid info (see FormResponse)
                                    return;
                                }
                                int serverNumber = response.getClickedButtonId() + 1;
                                String serverName = config.getString("Form.ServerName" + serverNumber);
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
                                    String noServer = "Failed to find a serverName value in the config for 'Button" + serverNumber + "'";
                                    player.sendMessage(noServer);
                                    GServerSelector.plugin.getLogger().warning(noServer);
                                }
                            }));
        } else {
            player.sendMessage("Sorry, this is a Bedrock command!");
        }
    }
}