package dev.projectg.geyserhub.module.message;

import dev.projectg.geyserhub.GeyserHubMain;
import me.clip.placeholderapi.PlaceholderAPI;
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
        List<String> messages = GeyserHubMain.getInstance().getConfig().getStringList("Join-Message.Messages");

        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, message)));
        }
    }
}
