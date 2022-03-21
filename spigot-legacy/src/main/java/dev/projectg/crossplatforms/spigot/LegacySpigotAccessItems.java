package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.accessitem.AccessItem;
import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.spigot.common.SpigotAccessItemsBase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacySpigotAccessItems extends SpigotAccessItemsBase {

    private static final String ITEM_IDENTIFIER = AccessItem.STATIC_IDENTIFIER;

    public LegacySpigotAccessItems(JavaPlugin plugin,
                                   ConfigManager configManager,
                                   ServerHandler serverHandler,
                                   InterfaceManager interfaceManager,
                                   BedrockHandler bedrockHandler,
                                   PlaceholderHandler placeholders) {
        super(plugin, configManager, serverHandler, interfaceManager, bedrockHandler, placeholders);
    }

    @Override
    public void setItemId(@NotNull ItemStack itemStack, @NotNull String identifier) {
        NbtUtils.setCustomString(itemStack, ITEM_IDENTIFIER, identifier);
    }

    @Nullable
    @Override
    public String getItemId(@NotNull ItemStack itemStack) {
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
