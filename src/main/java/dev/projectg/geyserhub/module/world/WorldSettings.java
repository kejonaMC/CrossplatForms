package dev.projectg.geyserhub.module.world;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;


public class WorldSettings implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-fall-damage")
                && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);

        else if (GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-drowning")
                && event.getCause() == EntityDamageEvent.DamageCause.DROWNING)
            event.setCancelled(true);

        else if (GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-fire-damage")
                && (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause()
                == EntityDamageEvent.DamageCause.LAVA))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-hunger-loss"))
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread(BlockIgniteEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-block-fire-spread"))
            return;
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-disable-block-burn"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeafDecay(LeavesDecayEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable_block-leaf-decay"))
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-mob-spawning"))
            return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-weather-change"))
            return;
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-player-pvp"))
            return;
        if (!(event.getEntity() instanceof Player)) return;
        event.setCancelled(true);

    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-block-break")
                || event.isCancelled())
            return;
        Player player = event.getPlayer();
        if (player.hasPermission("geyserhub.blockbreak")) {
            return;
        }
        player.sendMessage(ChatColor.RESET + "You can't break blocks here!");
        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("World-settings.disable-block-place")
                || event.isCancelled())
            return;
        ItemStack item = event.getItemInHand();
        if (item.getType() == Material.AIR)
            return;
        Player player = event.getPlayer();
         if (player.hasPermission("geyserhub.blockplace")) {
             return;
         }
        player.sendMessage(ChatColor.RESET + "You can't place blocks here!");
        event.setCancelled(true);
    }


}
