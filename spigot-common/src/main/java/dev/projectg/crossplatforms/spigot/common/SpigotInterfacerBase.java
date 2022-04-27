package dev.projectg.crossplatforms.spigot.common;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.java.ItemButton;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public abstract class SpigotInterfacerBase extends InterfaceManager implements Listener {

    public abstract void setMenuName(@Nonnull ItemStack stack, @Nonnull String identifier);

    /**
     * Attempt to retrieve the menu name that an ItemStack is contained in
     * @param stack The ItemStack to check
     * @return The menu name if the ItemStack contained the menu name, null if not. ItemStacks with null ItemMeta will always return null.
     */
    @Nullable
    public abstract String getMenuName(@Nonnull ItemStack stack);

    /**
     * Attempt to retrieve the menu that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The menu if the ItemStack contained the menu name and the menu exists. If no menu name was contained or the menu contained doesn't exist, this will return null.
     */
    @Nullable
    public JavaMenu getMenu(@Nonnull ItemStack itemStack, @Nonnull JavaMenuRegistry menuRegistry) {
        String menuName = getMenuName(itemStack);
        if (menuName == null) {
            return null;
        } else {
            return menuRegistry.getMenu(menuName);
        }
    }

    public void sendMenu(FormPlayer formPlayer, JavaMenu menu) {
        Logger logger = Logger.getLogger();
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        Player player = Objects.requireNonNull(Bukkit.getPlayer(formPlayer.getUuid()));

        Inventory selectorGUI; // todo: better size validation?
        if (menu.getSize() == JavaMenu.HOPPER_SIZE) {
            selectorGUI = Bukkit.createInventory(player, InventoryType.HOPPER, placeholders.setPlaceholders(formPlayer, menu.getTitle()));
        } else {
            selectorGUI = Bukkit.createInventory(player, menu.getSize(), placeholders.setPlaceholders(formPlayer, menu.getTitle()));
        }

        Map<Integer, ItemButton> buttons = menu.getButtons();
        for (Integer slot : buttons.keySet()) {
            ItemButton button = buttons.get(slot);

            Material material = Material.matchMaterial(button.getMaterial());
            if (material == null) {
                logger.severe("Java Button: " + menu.getIdentifier() + "." + slot + " will be stone because '" + button.getMaterial() +"' failed to map to a valid Spigot Material.");
                material = Material.STONE;
            }
            // todo: merge item construction logic with stuff from access items
            // Construct the item
            ItemStack item = new ItemStack(material);

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                logger.severe("Java Button: " + menu.getIdentifier() + "." + slot + " with Material: " + button.getMaterial() + " returned null ItemMeta, not adding the button!");
            } else {
                meta.setDisplayName(placeholders.setPlaceholders(formPlayer, button.getDisplayName()));
                meta.setLore(placeholders.setPlaceholders(formPlayer, button.getLore()));
                item.setItemMeta(meta);
                setMenuName(item, menu.getIdentifier());
                selectorGUI.setItem(slot, item);
            }
        }

        player.openInventory(selectorGUI);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // This is used for processing inventory clicks WITHIN the java menu GUI
        if (!javaRegistry.isEnabled()) {
            return;
        }

        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();

            if (item != null) {
                JavaMenu menu = getMenu(item, javaRegistry);
                if (menu != null) {
                    event.setCancelled(true);
                    // delegate action handling
                    int slot = event.getSlot();
                    if (menu.isButton(slot)) {
                        if (menu.isAutoClose()) {
                            // only close the inventory if the slot was a button and the menu is set to auto close
                            player.closeInventory();
                        }
                        menu.process(slot, event.isRightClick(), new SpigotPlayer(player));
                    }
                }
            }
        }
    }
}
