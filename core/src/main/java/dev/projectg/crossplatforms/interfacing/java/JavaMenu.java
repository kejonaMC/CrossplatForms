package dev.projectg.crossplatforms.interfacing.java;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.interfacing.ClickAction;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.utils.PlaceholderHandler;
import lombok.Getter;
import lombok.ToString;
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

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class JavaMenu extends Interface {

    public static final int MAX_SIZE = 54;
    public static final int HOPPER_SIZE = 5;

    public static final NamespacedKey BUTTON_KEY = new NamespacedKey(CrossplatForms.getInstance(), "crossplatFormsButton");
    public static final PersistentDataType<String, String> BUTTON_KEY_TYPE = PersistentDataType.STRING;

    protected transient final String permissionBase = "crossplatforms.menu";

    private boolean allowBedrock = false;

    private int size = 5; // Hopper size
    private Map<Integer, ItemButton> buttons = Collections.emptyMap();

    @Override
    public void send(@Nonnull dev.projectg.crossplatforms.handler.Player recipient) {
        Logger logger = Logger.getLogger();
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        if (!(recipient.getHandle() instanceof Player player)) {
            throw new AssertionError();
        }

        Inventory selectorGUI;
        if (size == HOPPER_SIZE) {
            selectorGUI = Bukkit.createInventory(player, InventoryType.HOPPER, placeholders.setPlaceholders(recipient, title));
        } else {
            selectorGUI = Bukkit.createInventory(player, size, placeholders.setPlaceholders(recipient, title));
        }

        for (Integer slot : buttons.keySet()) {
            ItemButton button = buttons.get(slot);

            Material material = Material.getMaterial(button.getMaterial());
            if (material == null) {
                logger.severe("Java Button: " + identifier + "." + slot + " will be stone because '" + button.getMaterial() +"' failed to map to a valid Spigot Material.");
                material = Material.STONE;
            }

            // Construct the item
            ItemStack item = new ItemStack(material);

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                logger.severe("Java Button: " + identifier + "." + slot + " with Material: " + button.getMaterial() + " returned null ItemMeta, not adding the button!");
            } else {
                meta.setDisplayName(placeholders.setPlaceholders(recipient, button.getDisplayName()));
                meta.setLore(placeholders.setPlaceholders(recipient, button.getLore()));
                meta.getPersistentDataContainer().set(BUTTON_KEY, PersistentDataType.STRING, identifier);
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
    public void process(int slot, boolean rightClick, @Nonnull dev.projectg.crossplatforms.handler.Player player, @Nonnull InterfaceManager interfaceManager) {
        if (isButton(slot)) {
            ItemButton button = buttons.get(slot);

            ClickAction any;
            if ((any = button.getAnyClick()) != null) {
                any.affectPlayer(player, interfaceManager);
            }

            if (rightClick) {
                ClickAction right;
                if ((right = button.getRightClick()) != null) {
                    right.affectPlayer(player, interfaceManager);
                }
            } else {
                ClickAction left;
                if ((left = button.getLeftClick()) != null) {
                    left.affectPlayer(player, interfaceManager);
                }
            }
        }
    }
}
