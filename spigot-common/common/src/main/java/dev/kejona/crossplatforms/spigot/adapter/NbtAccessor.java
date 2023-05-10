package dev.kejona.crossplatforms.spigot.adapter;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface NbtAccessor {

    @Nullable
    String getCustomString(@Nonnull ItemStack stack, @Nonnull String key);

    void setCustomString(@Nonnull ItemStack stack, @Nonnull String key, @Nonnull String value);
}
