package dev.projectg.geyserhub.listeners;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.bedrockmenu.BedrockMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;

public class SelectorItem implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (GeyserHubMain.getInstance().getConfig().getBoolean("ItemJoin")) {
            boolean isFloodgatePlayer = FloodgateApi.getInstance().isFloodgatePlayer(e.getPlayer().getUniqueId());
            if (isFloodgatePlayer) {
                Player player = e.getPlayer();
                ItemStack compass = BedrockMenu.getItem();
                if (player.getInventory().contains(compass)) {
                    return;
                }

                int desiredSlot = GeyserHubMain.getInstance().getConfig().getInt("Slot");
                ItemStack oldItem = player.getInventory().getItem(desiredSlot);
                if (oldItem == null || oldItem.getType() == Material.AIR) {
                    player.getInventory().setItem(desiredSlot, compass);
                } else {
                    for (int i = 0; i < 10; i++) {
                        if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) {
                            player.getInventory().setItem(i, oldItem);
                            player.getInventory().setItem(desiredSlot, compass);
                            break;
                        }
                    }
                    // If the player doesn't have the space in their hotbar then they don't get it
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player.getInventory().getItemInMainHand().isSimilar(BedrockMenu.getItem())) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    BedrockMenu.sendForm(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(BedrockMenu.getItem())) {
            if (!GeyserHubMain.getInstance().getConfig().getBoolean("AllowItemDrop")) {
                event.setCancelled(true);
            } else if (GeyserHubMain.getInstance().getConfig().getBoolean("DestroyDroppedItem")) {
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!GeyserHubMain.getInstance().getConfig().getBoolean("AllowItemMove") && event.getCurrentItem().isSimilar(BedrockMenu.getItem())) {
            event.setCancelled(true);
        }
    }
}
