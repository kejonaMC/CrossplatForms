package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.java.ItemButton;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.spigot.handler.SpigotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class MenuHelper implements Listener {

    private static final int HOPPER_SIZE = 5;
    private static final NamespacedKey BUTTON_KEY = new NamespacedKey(CrossplatFormsSpigot.getInstance(), ItemButton.STATIC_IDENTIFIER);
    private static final PersistentDataType<String, String> BUTTON_KEY_TYPE = PersistentDataType.STRING;

    private final InterfaceManager interfaceManager;
    private final JavaMenuRegistry javaMenuRegistry;

    public MenuHelper(InterfaceManager interfaceManager) {
        this.interfaceManager = interfaceManager;
        this.javaMenuRegistry = interfaceManager.getJavaRegistry();
    }

    /**
     * Attempt to retrieve the menu that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The menu if the ItemStack contained the menu name and the menu exists. If no menu name was contained or the menu contained doesn't exist, this will return null.
     */
    @Nullable
    public static JavaMenu getMenu(@Nonnull ItemStack itemStack, @Nonnull JavaMenuRegistry menuRegistry) {
        String menuName = getMenuName(itemStack);
        if (menuName == null) {
            return null;
        } else {
            return menuRegistry.getMenu(menuName);
        }
    }

    /**
     * Attempt to retrieve the menu name that an ItemStack is contained in
     * @param itemStack The ItemStack to check
     * @return The menu name if the ItemStack contained the menu name, null if not. ItemStacks with null ItemMeta will always return null.
     */
    @Nullable
    public static String getMenuName(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            return meta.getPersistentDataContainer().get(BUTTON_KEY, BUTTON_KEY_TYPE);
        }
        return null;
    }

    public static void sendMenu(FormPlayer formPlayer, JavaMenu menu) {
        Logger logger = Logger.getLogger();
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        Player player = Objects.requireNonNull(Bukkit.getPlayer(formPlayer.getUuid()));

        Inventory selectorGUI; // todo: better size validation?
        if (menu.getSize() == HOPPER_SIZE) {
            selectorGUI = Bukkit.createInventory(player, InventoryType.HOPPER, placeholders.setPlaceholders(formPlayer, menu.getTitle()));
        } else {
            selectorGUI = Bukkit.createInventory(player, menu.getSize(), placeholders.setPlaceholders(formPlayer, menu.getTitle()));
        }

        Map<Integer, ItemButton> buttons = menu.getButtons();
        for (Integer slot : buttons.keySet()) {
            ItemButton button = buttons.get(slot);

            Material material = Material.getMaterial(button.getMaterial());
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
                meta.getPersistentDataContainer().set(BUTTON_KEY, BUTTON_KEY_TYPE, menu.getIdentifier());
                item.setItemMeta(meta);
                selectorGUI.setItem(slot, item);
            }
        }

        player.openInventory(selectorGUI);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // This is used for processing inventory clicks WITHIN the java menu GUI

        if (javaMenuRegistry.isEnabled()) {
            if (event.getWhoClicked() instanceof Player player) {
                ItemStack item = event.getCurrentItem();

                if (item != null) {
                    JavaMenu menu = getMenu(item, interfaceManager.getJavaRegistry());
                    if (menu != null) {
                        event.setCancelled(true);
                        menu.process(event.getSlot(), event.isRightClick(), new SpigotPlayer(player), interfaceManager);
                    }
                }
            }
        }
    }
}
