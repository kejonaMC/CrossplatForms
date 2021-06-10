package dev.projectg.geyserhub.module.listeners;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.module.menu.SelectorItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ItemOnJoin implements Listener {

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().setHeldItemSlot(GeyserHubMain.getInstance().getConfig().getInt("Slot"));
        if (GeyserHubMain.getInstance().getConfig().getBoolean("Item-Join")) {
            Player player = event.getPlayer();
            ItemStack compass = SelectorItem.getItem();
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
