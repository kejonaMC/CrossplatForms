package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.accessitem.AccessItem;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.permission.Permissions;
import dev.kejona.crossplatforms.spigot.common.SpigotAccessItemsBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class LegacySpigotAccessItems extends SpigotAccessItemsBase {

    private static final String ITEM_IDENTIFIER = AccessItem.STATIC_IDENTIFIER;

    public LegacySpigotAccessItems(JavaPlugin plugin,
                                   ConfigManager configManager,
                                   Permissions permissions,
                                   Interfacer interfacer,
                                   BedrockHandler bedrockHandler,
                                   Placeholders placeholders) {
        super(plugin, configManager, permissions, interfacer, bedrockHandler, placeholders);
    }

    @Override
    public void setItemId(@Nonnull ItemStack itemStack, @Nonnull String identifier) {
        NbtUtils.setCustomString(itemStack, ITEM_IDENTIFIER, identifier);
    }

    @Nullable
    @Override
    public String getItemId(@Nonnull ItemStack itemStack) {
        return NbtUtils.getString(itemStack, ITEM_IDENTIFIER);
    }

    /**
     * Implemented individually here because this event is deprecated on newer versions
     */
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) { // Stop players without possession permission to pickup items
        handlePlayerPickupItem(event.getPlayer(), event.getItem(), event::setCancelled);
    }
}
