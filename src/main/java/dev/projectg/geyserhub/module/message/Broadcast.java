package dev.projectg.geyserhub.module.message;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.utils.bstats.SelectorLogger;
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

            if (GeyserHubMain.getInstance().getConfig().getBoolean("Broadcasts-enabled")) {
                ConfigurationSection parentSection = GeyserHubMain.getInstance().getConfig().getConfigurationSection("Broadcasts");
                if (parentSection == null) {
                    SelectorLogger.getLogger().severe("Broadcast configuration section is malformed, unable to send.");
                    return;
                }

                String broadcastId = getRandomElement(new ArrayList<>(parentSection.getKeys(false)));
                ConfigurationSection broadcast = parentSection.getConfigurationSection(broadcastId);
                assert broadcast != null;

                if (broadcast.contains("Messages", true) && broadcast.isList("Messages")) {
                    for (String message : broadcast.getStringList("Messages")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, message)));
                        }
                    }
                } else {
                    SelectorLogger.getLogger().severe("Broadcast with ID " + broadcastId + " has a malformed message list, unable to send.");
                }
            }
            startBroadcastTimer(scheduler);
        }, GeyserHubMain.getInstance().getConfig().getLong("Broadcast-interval"));
    }

    private static String getRandomElement(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
