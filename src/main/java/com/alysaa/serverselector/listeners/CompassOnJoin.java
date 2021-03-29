package com.alysaa.serverselector.listeners;

import com.alysaa.serverselector.GServerSelector;
import com.alysaa.serverselector.utils.CheckJavaOrFloodPlayer;
import com.alysaa.serverselector.utils.SelectorForm;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CompassOnJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (GServerSelector.plugin.getConfig().getBoolean("ItemJoin")) {
            boolean isFloodgatePlayer = CheckJavaOrFloodPlayer.isFloodgatePlayer(e.getPlayer().getUniqueId());
            if (isFloodgatePlayer) {
                Player player = e.getPlayer();
                ItemStack compass = new ItemStack(Material.COMPASS);
                ItemMeta compassMeta = compass.getItemMeta();
                compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Server Selector"));
                compass.setItemMeta(compassMeta);
                player.getInventory().setItem(GServerSelector.plugin.getConfig().getInt("Slot"), compass);
            }
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
               SelectorForm.SelectServer();
            }
        }
    }
}
