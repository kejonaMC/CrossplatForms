package dev.projectg.geyserhub.module.message;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class BroadCast {
    public static void startBroadcastTimer(BukkitScheduler scheduler) {
        int scheduleId = scheduler.scheduleSyncDelayedTask(GeyserHubMain.getInstance(), () -> {
            if (GeyserHubMain.getInstance().getConfig().getBoolean("Broadcasts-enabled")) {
                Set<String> broadcastsList = Objects.requireNonNull(GeyserHubMain.getInstance().getConfig().getConfigurationSection("Broadcasts")).getKeys(false);
                String broadcastId = getRandomElement(broadcastsList);
                ConfigurationSection broadcast = GeyserHubMain.getInstance().getConfig().getConfigurationSection("Broadcasts." + broadcastId);
                assert broadcast != null;
                for (String message : broadcast.getStringList("Messages")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            }
            startBroadcastTimer(scheduler);
        }, GeyserHubMain.getInstance().getConfig().getLong("Broadcast-interval"));
    }

    private static String getRandomElement(Set<String> set) {
        int index = new Random().nextInt(set.size());
        Iterator<String> iter = set.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }
}
