package com.alysaa.serverselector.form;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.utils.CheckJavaOrFloodPlayer;
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
                            .responseHandler((form, responseData) -> {
                                SimpleFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) {
                                    // player closed the form or returned invalid info (see FormResponse)
                                    return;
                                }
                                if (response.getClickedButtonId() == 0) {
                                    String server1 = config.getString("Form.ServerName1");
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF(server1);
                                    } catch (IOException eee) {
                                        Bukkit.getLogger().info("You'll never see me!");
                                    }
                                    player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", b.toByteArray());
                                }
                                if (response.getClickedButtonId() == 1) {
                                    String server2 = config.getString("Form.ServerName2");
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF(server2);
                                    } catch (IOException eee) {
                                        Bukkit.getLogger().info("You'll never see me!");
                                    }
                                    player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", b.toByteArray());
                                }
                                if (response.getClickedButtonId() == 2) {
                                    String server3 = config.getString("Form.ServerName3");
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF(server3);
                                    } catch (IOException eee) {
                                        Bukkit.getLogger().info("You'll never see me!");
                                    }
                                    player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", b.toByteArray());
                                }
                                if (response.getClickedButtonId() == 3) {
                                    String server4 = config.getString("Form.ServerName4");
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF(server4);
                                    } catch (IOException eee) {
                                        Bukkit.getLogger().info("You'll never see me!");
                                    }
                                    player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", b.toByteArray());
                                }
                                if (response.getClickedButtonId() == 4) {
                                    String server5 = config.getString("Form.ServerName5");
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);
                                    try {
                                        out.writeUTF("Connect");
                                        out.writeUTF(server5);
                                    } catch (IOException eee) {
                                        Bukkit.getLogger().info("You'll never see me!");
                                    }
                                    player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", b.toByteArray());
                                }
                            }));
        } else {
            player.sendMessage("Sorry this is a Bedrock command!");
        }
    }
}