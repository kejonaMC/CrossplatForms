package dev.projectg.geyserhub.listeners;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class MessageJoin implements Listener {

    @SuppressWarnings("unused")
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        List<String> messages = GeyserHubMain.getInstance().getConfig().getStringList("Join-Message");

        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{playerName}", player.getName())));
        }
    }
}
