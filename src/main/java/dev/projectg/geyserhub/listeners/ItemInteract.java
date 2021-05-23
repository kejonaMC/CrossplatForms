package dev.projectg.geyserhub.listeners;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.bedrockmenu.BedrockMenu;
import dev.projectg.geyserhub.javamenu.JavaMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Objects;

@SuppressWarnings("unused")
public class ItemInteract implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("Allow-Item-Move") && Objects.requireNonNull(event.getCurrentItem()).isSimilar(BedrockMenu.getItem())) {
                event.setCancelled(true);
            }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().isSimilar(BedrockMenu.getItem())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    BedrockMenu.getInstance().sendForm(player);
                } else {
                    JavaMenu.openMenu(player, GeyserHubMain.getInstance().getConfig());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(BedrockMenu.getItem())) {
            if (!GeyserHubMain.getInstance().getConfig().getBoolean("Allow-Item-Drop")) {
                event.setCancelled(true);
            } else if (GeyserHubMain.getInstance().getConfig().getBoolean("Destroy-Dropped-Item")) {
                event.getItemDrop().remove();
            }
        }
    }
}
