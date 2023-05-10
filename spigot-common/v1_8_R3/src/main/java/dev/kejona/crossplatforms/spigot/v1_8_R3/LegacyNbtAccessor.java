package dev.kejona.crossplatforms.spigot.v1_8_R3;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Uses the 3rd party NBT api
 */
public class LegacyNbtAccessor implements NbtAccessor {

    /**
     * NBTCompound name that the NBT of PersistentDataContainers in 1.14+ is stored in. We store it in the same place on
     * legacy versions in order for existing things to not break when switching server versions.
     */
    private static final String BUKKIT_COMPOUND = "PublicBukkitValues";

    private final String namespacePrefix;

    /**
     * @param plugin The plugin whose namespace should be used for writing/reading custom data
     */
    public LegacyNbtAccessor(Plugin plugin) {
        namespacePrefix = plugin.getName().toLowerCase(Locale.ROOT) + ":";
    }

    private String applyNamespace(String key) {
        return namespacePrefix + key;
    }

    @Override
    public @Nullable String getCustomString(@Nonnull ItemStack stack, @Nonnull String key) {
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

    @Override
    public void setCustomString(@Nonnull ItemStack stack, @Nonnull String key, @Nonnull String value) {
        // get existing or add empty compound for PublicBukkitValues
        // then set string with our namespace applied
        new NBTItem(stack, true).addCompound(BUKKIT_COMPOUND).setString(applyNamespace(key), value);
    }
}
