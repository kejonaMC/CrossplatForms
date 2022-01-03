package dev.projectg.crossplatforms.config.mapping.java;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.form.InterfaceUtils;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Menu {

    public static final int MAX_SIZE = 54;
    public static final int HOPPER_SIZE = 5;

    public static final NamespacedKey BUTTON_KEY = new NamespacedKey(CrossplatForms.getInstance(), "geyserHubButton");
    public static final PersistentDataType<String, String> BUTTON_KEY_TYPE = PersistentDataType.STRING;

    /**
     * Named ID of the menu.
     */
    @NodeKey
    private String name;

    /**
     * Title within the inventory that the player sees
     */
    private String title = "";
    private int size = 5; // Hopper size
    private Map<Integer, ItemButton> buttons = Collections.emptyMap();

    public void sendMenu(@Nonnull Player player) {
        Logger logger = CrossplatForms.getInstance().getLogger();

        Inventory selectorGUI;
        if (size == HOPPER_SIZE) {
            selectorGUI = Bukkit.createInventory(player, InventoryType.HOPPER, PlaceholderUtils.setPlaceholders(player, title));
        } else {
            selectorGUI = Bukkit.createInventory(player, size, PlaceholderUtils.setPlaceholders(player, title));
        }

        for (Integer slot : buttons.keySet()) {
            ItemButton button = buttons.get(slot);

            Material material = Material.getMaterial(button.getMaterial());
            if (material == null) {
                logger.severe("Java Button: " + name + "." + slot + " will be stone because '" + button.getMaterial() +"' failed to map to a valid Spigot Material.");
                material = Material.STONE;
            }

            // Construct the item
            ItemStack item = new ItemStack(material);

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                logger.severe("Java Button: " + name + "." + slot + " with Material: " + button.getMaterial() + " returned null ItemMeta, not adding the button!");
            } else {
                meta.setDisplayName(PlaceholderUtils.setPlaceholders(player, button.getDisplayName()));
                meta.setLore(PlaceholderUtils.setPlaceholders(player, button.getLore()));
                meta.getPersistentDataContainer().set(BUTTON_KEY, PersistentDataType.STRING, name);
                item.setItemMeta(meta);
                selectorGUI.setItem(slot, item);
            }
        }

        player.openInventory(selectorGUI);
    }

    /**
     * @param slot The inventory slot
     * @return If there is a button at the given inventory slot
     */
    public boolean isButton(int slot) {
        return buttons.get(slot) != null;
    }

    /**
     * Process a button click by a player in the menu.
     * @param slot The slot in the inventory. Nothing will happen if the slot does not contain a button.
     * @param rightClick True if it was a right click, false if a left click.
     * @param player the Player who clicked on the button.
     */
    public void process(int slot, boolean rightClick, @Nonnull Player player) {
        if (isButton(slot)) {
            ItemButton button = buttons.get(slot);

            InterfaceUtils.affectPlayer(button.getAnyClick(), player);

            if (rightClick) {
                InterfaceUtils.affectPlayer(button.getRightClick(), player);
            } else {
                InterfaceUtils.affectPlayer(button.getLeftClick(), player);
            }
        }
    }
}
