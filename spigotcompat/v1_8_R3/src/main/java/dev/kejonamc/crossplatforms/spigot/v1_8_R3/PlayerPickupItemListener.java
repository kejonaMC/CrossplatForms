package dev.kejonamc.crossplatforms.spigot.v1_8_R3;

import dev.kejona.crossplatforms.spigot.common.SpigotAccessItems;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@AllArgsConstructor
public class PlayerPickupItemListener implements Listener {

    private final SpigotAccessItems items;

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        items.handlePlayerPickupItem(event.getPlayer(), event.getItem(), event);
    }
}
