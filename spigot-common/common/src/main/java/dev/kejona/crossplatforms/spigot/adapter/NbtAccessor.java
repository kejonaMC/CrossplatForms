package dev.kejona.crossplatforms.spigot.adapter;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface NbtAccessor {

    @Nullable
    String getCustomString(@Nonnull ItemStack stack, @Nonnull String key);
    void setCustomString(@Nonnull ItemStack stack, @Nonnull String key, @Nonnull String value);

    void setCustomModelData(@Nonnull ItemStack stack, @Nullable Integer value);

    @Nonnull
    default ItemMeta requireItemMeta(ItemStack stack) {
        if (!stack.hasItemMeta()) {
            throw new IllegalArgumentException("ItemStack does not have ItemMeta: " + stack);
        }
        return stack.getItemMeta();
    }
}
