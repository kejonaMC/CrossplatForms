package com.alysaa.serverselector.utils;

import com.alysaa.serverselector.GServerSelector;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class SelectorForm {
    public static void SelectServer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(uuid);
            if (isFloodgatePlayer) {
                FloodgatePlayer fplayer = FloodgateApi.getInstance().getPlayer(uuid);
                fplayer.sendForm(
                        SimpleForm.builder()
                                .title(GServerSelector.plugin.getConfig().getString("Form.Title"))
                                .content(GServerSelector.plugin.getConfig().getString("Form.Content"))
                                .button(GServerSelector.plugin.getConfig().getString("Form.Button1"), FormImage.Type.PATH, "plugins/GServerSelector/ButtonImages/"+GServerSelector.plugin.getConfig().getString("Form.Path1"))
                                .button(GServerSelector.plugin.getConfig().getString("Form.Button2"), FormImage.Type.PATH, "plugins/GServerSelector/ButtonImages/"+GServerSelector.plugin.getConfig().getString("Form.Path2"))
                                .button(GServerSelector.plugin.getConfig().getString("Form.Button3"), FormImage.Type.PATH, "plugins/GServerSelector/ButtonImages/"+GServerSelector.plugin.getConfig().getString("Form.Path3"))
                                .button(GServerSelector.plugin.getConfig().getString("Form.Button4"), FormImage.Type.PATH, "plugins/GServerSelector/ButtonImages/"+GServerSelector.plugin.getConfig().getString("Form.Path4"))
                                .button(GServerSelector.plugin.getConfig().getString("Form.Button5"), FormImage.Type.PATH, "plugins/GServerSelector/ButtonImages/"+GServerSelector.plugin.getConfig().getString("Form.Path5"))
                                .responseHandler((form, responseData) -> {
                                    SimpleFormResponse response = form.parseResponse(responseData);
                                    if (response.getClickedButtonId() == 0) {
                                        String server1 = GServerSelector.plugin.getConfig().getString("Form.ServerName1");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server1);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());
                                    }
                                    if (response.getClickedButtonId() == 1) {
                                        String server2 = GServerSelector.plugin.getConfig().getString("Form.ServerName2");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server2);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());

                                    }
                                    if (response.getClickedButtonId() == 2) {
                                        String server3 = GServerSelector.plugin.getConfig().getString("Form.ServerName3");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server3);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());
                                    }
                                    if (response.getClickedButtonId() == 3) {
                                        String server4 = GServerSelector.plugin.getConfig().getString("Form.ServerName4");
                                        ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                        out.writeUTF("Connect");
                                        out.writeUTF(server4);

                                        player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());
                                    }
                                    String server5 = GServerSelector.plugin.getConfig().getString("Form.ServerName5");
                                    ByteArrayDataOutput out = ByteStreams.newDataOutput();

                                    out.writeUTF("Connect");
                                    out.writeUTF(server5);

                                    player.sendPluginMessage(GServerSelector.plugin, "BungeeCord", out.toByteArray());

                                }));
            }else {
                player.sendMessage("Sorry this is a Bedrock command!");
            }
        }
    }
}