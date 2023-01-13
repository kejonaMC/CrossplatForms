package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.spigot.handler.SpigotPlayer;
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

public class SpigotInterfacer extends Interfacer implements Listener {

    // Menus that are open
    private final Map<Inventory, JavaMenu> menuCache = new HashMap<>();
    // The active resolver for a menu that is open
    private final Map<Inventory, Resolver> resolverCache = new HashMap<>();

    @Override
    public void openInventory(FormPlayer recipient, JavaMenu menu, dev.kejona.crossplatforms.item.Inventory inventory, Resolver resolver) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(recipient.getUuid()), "player lookup");
        Inventory bukkitInventory = inventory.castedHandle();

        player.openInventory(bukkitInventory);
        menuCache.put(bukkitInventory, menu);
        resolverCache.put(bukkitInventory, resolver);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // This is used for processing inventory clicks WITHIN the java menu GUI

        if (javaRegistry.isEnabled()) {
            if (event.getWhoClicked() instanceof Player) {
                Inventory inventory = event.getClickedInventory(); // inventory that was clicked in
                if (inventory == null) {
                    // clicked outside of window
                    return;
                }

                if (menuCache.containsKey(inventory)) {
                    // handle clicking in the menu inventory
                    event.setCancelled(true);
                    menuCache.get(inventory).process(
                        event.getSlot(),
                        event.isRightClick(),
                        new SpigotPlayer((Player) event.getWhoClicked()),
                        resolverCache.get(inventory)
                    );
                } else if (event.isShiftClick()) {
                    // stop players from shift-clicking items into the menu's inventory.
                    if (menuCache.containsKey(event.getInventory())) { // the upper inventory
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        menuCache.remove(inventory);
        resolverCache.remove(inventory);
    }
}
