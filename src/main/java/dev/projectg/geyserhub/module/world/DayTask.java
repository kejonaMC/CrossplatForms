package dev.projectg.geyserhub.module.world;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class DayTask  extends BukkitRunnable {
    @Override
    public void run() {
        String world = GeyserHubMain.getInstance().getConfig().getString("World-settings.World-name");
        assert world != null;
        if (Bukkit.getServer().getWorld(world) != null){
            Objects.requireNonNull(Bukkit.getServer().getWorld(world)).setTime(0L);
        }else{
         System.out.println("[GeyserHub] The world currently set as lobby does not exist. Edit the config.yml");
        }
    }

}
