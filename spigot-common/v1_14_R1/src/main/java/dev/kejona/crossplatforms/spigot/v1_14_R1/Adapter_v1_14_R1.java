package dev.kejona.crossplatforms.spigot.v1_14_R1;

import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import dev.kejona.crossplatforms.spigot.utils.InventoryUtils;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Adapter_v1_14_R1 extends Adapter_v1_13_R2 {

    /**
     * Custom model data was added in 1.14
     * @return true
     */
    @Override
    public boolean customModelData() {
        return true;
    }

    @Override
    public void setCustomModelData(@Nonnull ItemStack stack, @Nullable Integer value) {
        ItemMeta meta = InventoryUtils.requireItemMeta(stack);
        meta.setCustomModelData(value);
        stack.setItemMeta(meta);
    }

    @Override
    public NbtAccessor nbtAccessor(Plugin plugin) {
        return new ModernNbtAccessor(plugin);
    }
}
