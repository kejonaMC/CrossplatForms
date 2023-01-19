package dev.kejona.crossplatforms.spigot.adapter;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface VersionAdapter {

    boolean customModelData();

    void setCustomModelData(@Nonnull ItemStack stack, @Nullable Integer value);

    Material playerHeadMaterial();

    void setSkullProfile(SkullMeta meta, FormPlayer player);

    NbtAccessor nbtAccessor(Plugin plugin);

    void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items);
}
