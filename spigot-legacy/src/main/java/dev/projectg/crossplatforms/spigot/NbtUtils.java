package dev.projectg.crossplatforms.spigot;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NbtUtils {

    private NbtUtils() {

    }

    @Nullable
    public static String getString(@Nonnull ItemStack stack, @Nonnull String key) {
        NBTItem item = new NBTItem(stack);
        return item.getString(key);
    }

    public static void setCustomString(@Nonnull ItemStack stack, @Nonnull String key, @Nonnull String value) {
        NBTItem item = new NBTItem(stack);
        item.setString(key, value);
        item.mergeCustomNBT(stack);
    }
}
