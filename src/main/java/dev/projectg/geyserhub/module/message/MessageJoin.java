package dev.projectg.geyserhub.module.message;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.utils.PlaceholderUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class MessageJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.MAIN);
        Player player = e.getPlayer();
        List<String> messages = config.getStringList("Join-Message.Messages");

        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PlaceholderUtils.setPlaceholders(player, message)));
        }
    }
}
