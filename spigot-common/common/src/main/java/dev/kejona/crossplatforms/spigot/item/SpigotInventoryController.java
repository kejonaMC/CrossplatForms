package dev.kejona.crossplatforms.spigot.item;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.inventory.ClickHandler;
import dev.kejona.crossplatforms.inventory.InventoryController;
import dev.kejona.crossplatforms.inventory.InventoryHandle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpigotInventoryController implements InventoryController, Listener {

    // Menus that are open
    private final Map<Inventory, ClickHandler> handlerCache = new HashMap<>();

    @Override
    public void openInventory(FormPlayer recipient, InventoryHandle container, ClickHandler clickHandler) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(recipient.getUuid()), "player lookup for " + recipient.getUuid());
        Inventory inventory = container.castedHandle(Inventory.class);

        player.openInventory(inventory);
        handlerCache.put(inventory, clickHandler);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // This is used for processing inventory clicks WITHIN the java menu GUI

        if (event.getWhoClicked() instanceof Player) {
            Inventory inventory = event.getClickedInventory(); // inventory that was clicked in
            if (inventory == null) {
                // clicked outside of window
                return;
            }

            if (handlerCache.containsKey(inventory)) {
                // handle clicking in the menu inventory
                event.setCancelled(true);
                handlerCache.get(inventory).handle(event.getSlot(), event.isRightClick());
            } else if (event.isShiftClick()) {
                // stop players from shift-clicking items into the menu's inventory.
                if (handlerCache.containsKey(event.getInventory())) { // the upper inventory
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        handlerCache.remove(event.getInventory());
    }
}
