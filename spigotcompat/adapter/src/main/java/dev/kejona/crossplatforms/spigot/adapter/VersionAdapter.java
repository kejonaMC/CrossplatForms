package dev.kejona.crossplatforms.spigot.adapter;

import org.bukkit.plugin.Plugin;

public interface VersionAdapter {

    boolean customModelData();

    NbtAccessor nbtAccessor(Plugin plugin);
}
