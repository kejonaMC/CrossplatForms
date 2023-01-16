package dev.kejona.crossplatforms.spigot.v1_14_R1;

import dev.kejona.crossplatforms.spigot.adapter.NbtAccessor;
import dev.kejona.crossplatforms.spigot.v1_13_R2.Adapter_v1_13_R2;
import org.bukkit.plugin.Plugin;

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
    public NbtAccessor nbtAccessor(Plugin plugin) {
        return new ModernNbtAccessor(plugin);
    }
}
