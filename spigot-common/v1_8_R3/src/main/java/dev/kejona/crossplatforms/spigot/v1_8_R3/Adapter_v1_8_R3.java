package dev.kejona.crossplatforms.spigot.v1_8_R3;

import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

public class Adapter_v1_8_R3 implements VersionAdapter {

    @Override
    public boolean customModelData() {
        return false;
    }

    @Override
    public Material playerHeadMaterial() {
        return Material.SKULL_ITEM;
    }

    @Override
    public NbtAccessor nbtAccessor(Plugin plugin) {
        return new LegacyNbtAccessor(plugin);
    }

    @Override
    public void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items) {
        PlayerPickupItemListener listener = new PlayerPickupItemListener(items);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
