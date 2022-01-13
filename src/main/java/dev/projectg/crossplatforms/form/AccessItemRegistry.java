package dev.projectg.crossplatforms.form;


import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class AccessItemRegistry implements Reloadable {

    private final CrossplatForms crossplatForms;

    @Getter
    private boolean enabled = false;

    @Getter
    private final Map<String, AccessItem> items = new HashMap<>();

    public AccessItemRegistry(CrossplatForms crossplatForms) {
        this.crossplatForms = crossplatForms;
        ReloadableRegistry.registerReloadable(this);
        load();
    }

    /**
     * Adds access items from the Access Items config.
     * Does not clear existing items.
     */
    private void load() {
        AccessItems config = crossplatForms.getConfigManager().getConfig(AccessItems.class);
        items.clear();
        if (enabled = config.isEnable()) {
            for (String identifier : config.getItems().keySet()) {
                AccessItem item = config.getItems().get(identifier);
                items.put(identifier, item);
            }
        }
    }

    @Override
    public boolean reload() {
        load();
        return true;
    }

    /**
     * Attempt to retrieve the Access Item that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The Access Item if the ItemStack contained the identifier of the Access Item, and the Access Item exists. Will return null if their conditions are false.
     */
    @Nullable
    public AccessItem getItem(@Nonnull ItemStack itemStack) {
        String identifier = getItemId(itemStack);
        if (identifier == null) {
            return null;
        } else {
            return items.get(identifier);
        }
    }

    /**
     * Attempt to retrieve the Access Item ID that an ItemStack points to. The Access Item ID may or may not refer
     * to an actual AccessItem
     * @param itemStack The ItemStack to check
     * @return The AccessItem ID if the ItemStack contained the name, null if not.
     */
    @Nullable
    public static String getItemId(@Nonnull ItemStack itemStack) {
        Objects.requireNonNull(itemStack);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        } else {
            return meta.getPersistentDataContainer().get(AccessItem.ACCESS_ITEM_KEY, AccessItem.ACCESS_ITEM_KEY_TYPE);
        }
    }
}
