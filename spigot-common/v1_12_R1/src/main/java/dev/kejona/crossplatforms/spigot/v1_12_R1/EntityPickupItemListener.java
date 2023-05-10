package dev.kejona.crossplatforms.spigot.v1_12_R1;

import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

@AllArgsConstructor
public class EntityPickupItemListener implements Listener {

    private final SpigotAccessItems items;

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            items.handlePlayerPickupItem((Player) event.getEntity(), event.getItem(), event);
        }
    }
}
