package dev.kejona.crossplatforms.spigot.pdc;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModernPDCAccessor implements PDCAccessor {

    private final Plugin plugin;

    public ModernPDCAccessor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @Nullable
    public String getCustomString(@Nonnull ItemStack stack, @Nonnull String key) {
        if (stack == null || !stack.hasItemMeta()) return null;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;

        NamespacedKey nKey = new NamespacedKey(plugin, key);
        if (!meta.getPersistentDataContainer().has(nKey, org.bukkit.persistence.PersistentDataType.STRING)) {
            return null;
        }
        return meta.getPersistentDataContainer().get(nKey, org.bukkit.persistence.PersistentDataType.STRING);
    }

    @Override
    public void setCustomString(@Nonnull ItemStack stack, @Nonnull String key, @Nonnull String value) {
        if (stack == null) return;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;

        NamespacedKey nKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(nKey, org.bukkit.persistence.PersistentDataType.STRING, value);
        stack.setItemMeta(meta);
    }
}
