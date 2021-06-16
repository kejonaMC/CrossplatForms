package dev.projectg.geyserhub.module.message;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class Broadcast {
    public static void startBroadcastTimer(BukkitScheduler scheduler) {
        int scheduleId = scheduler.scheduleSyncDelayedTask(GeyserHubMain.getInstance(), () -> {

            if (GeyserHubMain.getInstance().getConfig().getBoolean("Broadcasts.Enable", false)) {
                ConfigurationSection parentSection = GeyserHubMain.getInstance().getConfig().getConfigurationSection("Broadcasts.Messages");
                if (parentSection == null) {
                    SelectorLogger.getLogger().severe("Broadcasts.Messages configuration section is malformed, unable to send.");
                    return;
                }

                String broadcastId = getRandomElement(new ArrayList<>(parentSection.getKeys(false)));

                if (parentSection.contains(broadcastId, true) && parentSection.isList(broadcastId)) {
                    for (String message : parentSection.getStringList(broadcastId)) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, message)));
                        }
                    }
                } else {
                    SelectorLogger.getLogger().severe("Broadcast with ID " + broadcastId + " has a malformed message list, unable to send.");
                }
            }
            startBroadcastTimer(scheduler);
        }, GeyserHubMain.getInstance().getConfig().getLong("Broadcasts-Interval", 3600));
    }

    private static String getRandomElement(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
