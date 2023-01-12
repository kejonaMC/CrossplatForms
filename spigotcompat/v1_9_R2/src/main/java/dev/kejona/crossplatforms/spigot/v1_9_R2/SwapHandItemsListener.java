package dev.kejona.crossplatforms.spigot.v1_9_R2;

import dev.kejona.crossplatforms.spigot.common.SpigotAccessItems;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@AllArgsConstructor
public class SwapHandItemsListener implements Listener {

    @Nonnull
    private final SpigotAccessItems accessItems;

    @EventHandler
    public void PlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) { // Don't allow putting it in the offhand
        ItemStack item = event.getOffHandItem();
        if (item != null && accessItems.getItemId(item) != null) {
            event.setCancelled(true);
        }
    }
}
