package dev.projectg.geyserhub.module.items;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.module.menu.BedrockMenu;
import dev.projectg.geyserhub.module.menu.JavaMenu;
import dev.projectg.geyserhub.module.menu.SelectorItem;
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
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("Allow-Item-Move") && Objects.requireNonNull(event.getCurrentItem()).isSimilar(SelectorItem.getItem())) {
                event.setCancelled(true);
            }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().isSimilar(SelectorItem.getItem())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    BedrockMenu.getInstance().sendForm(FloodgateApi.getInstance().getPlayer(player.getUniqueId()));
                } else {
                    JavaMenu.openMenu(player, GeyserHubMain.getInstance().getConfig());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(SelectorItem.getItem())) {
            if (!GeyserHubMain.getInstance().getConfig().getBoolean("Allow-Item-Drop")) {
                event.setCancelled(true);
            } else if (GeyserHubMain.getInstance().getConfig().getBoolean("Destroy-Dropped-Item")) {
                event.getItemDrop().remove();
            }
        }
    }
}
