package dev.kejona.crossplatforms.spigot.v1_12_R1;

import dev.kejona.crossplatforms.spigot.SpigotAccessItems;
import dev.kejona.crossplatforms.spigot.v1_9_R2.Adapter_v1_9_R2;
import dev.kejona.crossplatforms.spigot.v1_9_R2.SwapHandItemsListener;
import org.bukkit.plugin.Plugin;

public class Adapter_v1_12_R1 extends Adapter_v1_9_R2 {

    @Override
    public void registerAuxiliaryEvents(Plugin plugin, SpigotAccessItems items) {
        // No super() because EntityPickupItemListener is used instead of PlayerPickupItemListener
        // because PlayerPickupItemEvent was deprecated in favour of EntityPickupItemEvent

        plugin.getServer().getPluginManager().registerEvents(new EntityPickupItemListener(items), plugin);
        plugin.getServer().getPluginManager().registerEvents(new SwapHandItemsListener(items), plugin);
    }
}
