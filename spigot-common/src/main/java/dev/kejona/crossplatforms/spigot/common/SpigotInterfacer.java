package dev.kejona.crossplatforms.spigot.common;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.interfacing.java.ItemButton;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.spigot.common.handler.SpigotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpigotInterfacer extends Interfacer implements Listener {

    private final Map<Inventory, JavaMenu> menuCache = new HashMap<>();

    @Override
    public void sendMenu(FormPlayer formPlayer, JavaMenu menu, @Nonnull Resolver resolver) {
        Logger logger = Logger.get();
        Player player = Objects.requireNonNull(Bukkit.getPlayer(formPlayer.getUuid()));

        Inventory selectorGUI; // todo: better size validation?
        if (menu.getSize() == JavaMenu.HOPPER_SIZE) {
            selectorGUI = Bukkit.createInventory(player, InventoryType.HOPPER, resolver.apply(menu.getTitle()));
        } else {
            selectorGUI = Bukkit.createInventory(player, menu.getSize(), resolver.apply(menu.getTitle()));
        }

        Map<Integer, ItemButton> buttons = menu.getButtons();
        for (Integer slot : buttons.keySet()) {
            ItemButton button = buttons.get(slot);

            String material = button.getMaterial();
            Material type;
            if (material != null) {
                type = Material.matchMaterial(button.getMaterial());
                if (type == null) {
                    logger.severe("Java Button: " + menu.getIdentifier() + "." + slot + " will be stone because '" + button.getMaterial() +"' failed to map to a valid Spigot Material.");
                    type = Material.STONE;
                }
            } else {
                type = Material.STONE;
            }

            // todo: merge item construction logic with stuff from access items
            // Construct the item
            ItemStack item = new ItemStack(type);

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                logger.severe("Java Button: " + menu.getIdentifier() + "." + slot + " with Material: " + button.getMaterial() + " returned null ItemMeta, not adding the button!");
            } else {
                meta.setDisplayName(resolver.apply(button.getDisplayName()));
                meta.setLore(resolver.apply(button.getLore()));
                item.setItemMeta(meta);
                selectorGUI.setItem(slot, item);
            }
        }

        player.openInventory(selectorGUI);
        menuCache.put(selectorGUI, menu);
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
                    menuCache.get(inventory).process(event.getSlot(), event.isRightClick(), new SpigotPlayer((Player) event.getWhoClicked()));
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
        menuCache.remove(event.getInventory());
    }
}
