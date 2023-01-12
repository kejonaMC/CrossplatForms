package dev.kejona.crossplatforms.spigot.v1_12_R1;

import dev.kejona.crossplatforms.spigot.common.SpigotAccessItems;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;
import dev.kejona.crossplatforms.spigot.v1_9_R2.SwapHandItemsListener;
import org.bukkit.plugin.Plugin;

public class Adapter_v1_12_R1 extends Adapter_v1_9_R2 {

    @Override
    public void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items) {
        plugin.getServer().getPluginManager().registerEvents(new EntityPlayerPickupItemListener(items), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SwapHandItemsListener(items), plugin);
    }
}
