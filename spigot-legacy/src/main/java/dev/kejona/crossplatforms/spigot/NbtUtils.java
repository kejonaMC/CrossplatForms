package dev.kejona.crossplatforms.spigot;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.kejona.crossplatforms.Constants;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NbtUtils {

    /**
     * This needs to match the plugin identifier in plugin.yml.
     */
    private static final String NAMESPACE_PREFIX = Constants.Id() + ":";

    /**
     * NBTCompound name that the NBT of PersistentDataContainers in 1.14+ is stored in. We store it in the same place on
     * legacy versions in order for existing things to not break when switching from SpigotLegacy to Spigot or vice versa.
     */
    private static final String BUKKIT_COMPOUND = "PublicBukkitValues";

    private NbtUtils() {

    }

    /**
     * Get the string value at a key within this plugins namespace. ItemStack may be air.
     * @param stack The itemstack to get the NBT from
     * @param key The key to get the value at, resolved within this plugins namespace
     * @return The string value, if present.
     */
    @Nullable
    public static String getString(@Nonnull ItemStack stack, @Nonnull String key) {
        if (stack.getItemMeta() == null) {
            // Check against air
            return null;
        }
        NBTCompound bukkitValues = new NBTItem(stack).getCompound(BUKKIT_COMPOUND);
        if (bukkitValues == null) {
            return null;
        } else {
            return bukkitValues.getString(applyNamespace(key));
        }
    }

    /**
     * Set a custom NBT tag on the itemstack. Vanilla tags will not be placed on the item. Tags are placed within this
     * plugin's namespace.
     * @param stack The ItemStack whose NBT to put the tag on
     * @param key The key of the tag
     * @param value The value of the tag
     * @throws NullPointerException If NBT cannot be written to the stack (i.e. is air)
     * @see NBTItem#mergeCustomNBT(ItemStack)
     */
    public static void setCustomString(@Nonnull ItemStack stack, @Nonnull String key, @Nonnull String value) {
        // get existing or add empty compound for PublicBukkitValues
        // then set string with our namespace applied
        new NBTItem(stack, true).addCompound(BUKKIT_COMPOUND).setString(applyNamespace(key), value);
    }

    private static String applyNamespace(String key) {
        return NAMESPACE_PREFIX + key;
    }
}
