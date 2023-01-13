package dev.kejona.crossplatforms.spigot.v1_9_R2;

import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import dev.kejonamc.crossplatforms.spigot.v1_8_R3.Adapter_v1_8_R3;
import org.bukkit.plugin.Plugin;

public class Adapter_v1_9_R2 extends Adapter_v1_8_R3 {

    @Override
    public void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items) {
        super.registerAuxiliaryEvents(plugin, items);
        // register the PlayerPickupItemListener

        // Offhand was introduced in 1.9
        plugin.getServer().getPluginManager().registerEvents(new SwapHandItemsListener(items), plugin);
    }
}
