package dev.kejonamc.crossplatforms.spigot.v1_14_R1;

import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import org.bukkit.plugin.Plugin;

public class Adapter_v1_14_R1 implements VersionAdapter {

    @Override
    public boolean customModelData() {
        return true;
    }

    @Override
    public NbtAccessor nbtAccessor(Plugin plugin) {
        return new ModernNbtAccessor(plugin);
    }
}
