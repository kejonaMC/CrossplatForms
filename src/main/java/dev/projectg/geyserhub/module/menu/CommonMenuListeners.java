package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@SuppressWarnings("unused")
public class CommonMenuListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("Selector-Item.Allow-Move") && Objects.requireNonNull(event.getCurrentItem()).isSimilar(AccessItem.getItem())) {
                event.setCancelled(true);
            }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(AccessItem.getItem())) {
            if (!GeyserHubMain.getInstance().getConfig().getBoolean("Selector-Item.Allow-Drop")) {
                event.setCancelled(true);
            } else if (GeyserHubMain.getInstance().getConfig().getBoolean("Selector-Item.Destroy-Dropped")) {
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().setHeldItemSlot(GeyserHubMain.getInstance().getConfig().getInt("Selector-Item.Slot"));
        if (GeyserHubMain.getInstance().getConfig().getBoolean("Selector-Item.Join")) {
            Player player = event.getPlayer();
            ItemStack accessItem = AccessItem.getItem();
            if (player.getInventory().contains(accessItem)) {
                return;
            }

            int desiredSlot = GeyserHubMain.getInstance().getConfig().getInt("Selector-Item.Slot");
            ItemStack oldItem = player.getInventory().getItem(desiredSlot);
            if (oldItem == null || oldItem.getType() == Material.AIR) {
                player.getInventory().setItem(desiredSlot, accessItem);
            } else {
                for (int i = 0; i < 10; i++) {
                    if (player.getInventory().getItem(i) == null || Objects.requireNonNull(player.getInventory().getItem(i)).getType() == Material.AIR) {
                        player.getInventory().setItem(i, oldItem);
                        player.getInventory().setItem(desiredSlot, accessItem);
                        break;
                    }
                }
                // If the player doesn't have the space in their hotbar then they don't get it
            }
        }
    }
}
