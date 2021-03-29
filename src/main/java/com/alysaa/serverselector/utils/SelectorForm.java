package com.alysaa.serverselector.utils;

import com.alysaa.serverselector.GServerSelector;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class SelectorForm {
    public static void SelectServer() {
        FileConfiguration config = GServerSelector.plugin.getConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
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
                                .responseHandler((form, responseData) -> {
                                    SimpleFormResponse response = form.parseResponse(responseData);
                                    if (!response.isCorrect()) {
                                        // player closed the form or returned invalid info (see FormResponse)
                                        return;
                                    }
                                    if (response.getClickedButtonId() == 1) {
                                        String server1 = config.getString("Form.ServerName1");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server1);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());
                                    }
                                    if (response.getClickedButtonId() == 2) {
                                        String server2 = config.getString("Form.ServerName2");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server2);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());

                                    }
                                    if (response.getClickedButtonId() == 3) {
                                        String server3 = config.getString("Form.ServerName3");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server3);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());
                                    }
                                    if (response.getClickedButtonId() == 4) {
                                        String server4 = config.getString("Form.ServerName4");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server4);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());
                                    }
                                    if (response.getClickedButtonId() == 5) {
                                        String server5 = config.getString("Form.ServerName5");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server5);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());
                                    }
                                }));
            }else {
                player.sendMessage("Sorry this is a Bedrock command!");
            }
        }
    }
}