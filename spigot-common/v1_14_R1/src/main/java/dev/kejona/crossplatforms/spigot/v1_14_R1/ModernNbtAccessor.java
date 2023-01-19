package dev.kejona.crossplatforms.spigot.v1_14_R1;

import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import lombok.AllArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static dev.kejona.crossplatforms.spigot.utils.InventoryUtils.requireItemMeta;

/**
 * Uses the Spigot Persistent Data Container api
 */
@AllArgsConstructor
public class ModernNbtAccessor implements NbtAccessor {

    private final Plugin plugin;

    @Nullable
    @Override
    public String getCustomString(@Nonnull ItemStack stack, @Nonnull String key) {
        if (!stack.hasItemMeta()) {
            return null;
        }

        return stack.getItemMeta().getPersistentDataContainer().get(
            new NamespacedKey(plugin, key),
            PersistentDataType.STRING
        );
    }

    @Override
    public void setCustomString(@Nonnull ItemStack stack, @Nonnull String key, @Nonnull String value) {
        requireItemMeta(stack).getPersistentDataContainer().set(
            new NamespacedKey(plugin, key),
            PersistentDataType.STRING,
            value
        );
    }
}
