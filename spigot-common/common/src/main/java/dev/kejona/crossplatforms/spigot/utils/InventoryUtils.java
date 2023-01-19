package dev.kejona.crossplatforms.spigot.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public final class InventoryUtils {

    private InventoryUtils() {

    }

    @Nonnull
    public static ItemMeta requireItemMeta(ItemStack stack) {
        if (!stack.hasItemMeta()) {
            throw new IllegalArgumentException("ItemStack does not have ItemMeta: " + stack);
        }
        return stack.getItemMeta();
    }
}
