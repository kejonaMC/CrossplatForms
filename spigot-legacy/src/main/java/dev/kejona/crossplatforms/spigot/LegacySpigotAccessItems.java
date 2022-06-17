package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.accessitem.AccessItem;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.spigot.common.SpigotAccessItemsBase;
import org.bukkit.entity.Player;
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
                                   ServerHandler serverHandler,
                                   Interfacer interfacer,
                                   BedrockHandler bedrockHandler,
                                   Placeholders placeholders) {
        super(plugin, configManager, serverHandler, interfacer, bedrockHandler, placeholders);
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
    public void onEntityPickupItem(PlayerPickupItemEvent event) { // Stop players without possession permission to pickup items
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        String id = getItemId(item);
        if (id != null) {
            AccessItem access = super.getItem(id);
            if (access == null) {
                event.setCancelled(true);
            } else if (!player.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                event.setCancelled(true);
            }
        }
    }
}
