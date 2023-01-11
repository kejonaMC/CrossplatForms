package dev.kejonamc.crossplatforms.spigot.v1_8_R3;

import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import org.bukkit.plugin.Plugin;

public class Adapter_v1_8_R3 implements VersionAdapter {

    @Override
    public boolean customModelData() {
        return false;
    }

    @Override
    public NbtAccessor nbtAccessor(Plugin plugin) {
        return new LegacyNbtAccessor(plugin);
    }
}
