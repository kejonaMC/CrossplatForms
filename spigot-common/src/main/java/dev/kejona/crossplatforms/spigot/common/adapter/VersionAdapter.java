package dev.kejona.crossplatforms.spigot.common.adapter;

import dev.kejona.crossplatforms.spigot.common.SpigotAccessItems;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public interface VersionAdapter {

    boolean customModelData();

    Material playerHeadMaterial();

    NbtAccessor nbtAccessor(Plugin plugin);

    void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items);
}
