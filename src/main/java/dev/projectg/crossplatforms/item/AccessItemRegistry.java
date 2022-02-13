package dev.projectg.crossplatforms.item;


import dev.projectg.crossplatforms.config.ConfigManager;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import dev.projectg.crossplatforms.reloadable.Reloadable;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccessItemRegistry implements Reloadable {

    private final ConfigManager configManager;
    private final ServerHandler serverHandler;

    @Getter
    private boolean enabled = false;

    /**
     * Set the held slot to the access item when given through events
     */
    @Getter
    @Accessors(fluent = true)
    private boolean setHeldSlot;

    @Getter
    private Map<AccessItem.Limit, PermissionDefault> globalPermissionDefaults = Collections.emptyMap();

    @Getter
    private final Map<String, AccessItem> items = new HashMap<>();

    public AccessItemRegistry(ConfigManager configManager, ServerHandler serverHandler) {
        this.configManager = configManager;
        this.serverHandler = serverHandler;
        ReloadableRegistry.register(this);
        load();
    }

    /**
     * Adds access items from the Access Items config.
     * Does not clear existing items.
     */
    private void load() {
        items.clear();

        if (configManager.getConfig(AccessItemConfig.class).isEmpty()) {
            enabled = false;
            return;
        }

        AccessItemConfig config = configManager.getConfig(AccessItemConfig.class).get();
        enabled = config.isEnable();
        if (enabled) {
            setHeldSlot = config.isSetHeldSlot();
            globalPermissionDefaults = config.getGlobalPermissionDefaults();

            for (String identifier : config.getItems().keySet()) {
                AccessItem item = config.getItems().get(identifier);
                items.put(identifier, item);

                // Register permissions with the server
                item.generatePermissions(this);
                for (Permission entry : item.getPermissions().values()) {
                    serverHandler.registerPermission(entry);
                }
            }
        }
    }

    @Override
    public boolean reload() {
        // Unregister permissions
        if (enabled) {
            for (AccessItem accessItem : items.values()) {
                for (Permission permission : accessItem.getPermissions().values()) {
                    serverHandler.unregisterPermission(permission.key());
                }
            }
        }

        load();
        return true;
    }

    /**
     * Attempt to retrieve the Access Item that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The Access Item if the ItemStack contained the identifier of the Access Item, and the Access Item exists. Will return null if their conditions are false.
     */
    @Nullable
    public AccessItem getItem(@Nonnull ItemStack itemStack) {
        String identifier = getItemId(itemStack);
        if (identifier == null) {
            return null;
        } else {
            return items.get(identifier);
        }
    }

    /**
     * Attempt to retrieve the Access Item from its identifier
     * @param id The identifier to check.
     * @return The Access Item that has the given identifier, if it exists.
     */
    @Nullable
    public AccessItem getItem(@Nonnull String id) {
        return items.get(id);
    }

    /**
     * Attempt to retrieve the Access Item ID that an ItemStack points to. The Access Item ID may or may not refer
     * to an actual AccessItem
     * @param itemStack The ItemStack to check
     * @return The AccessItem ID if the ItemStack contained the name, null if not.
     */
    @Nullable
    public static String getItemId(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        } else {
            return meta.getPersistentDataContainer().get(AccessItem.ACCESS_ITEM_KEY, AccessItem.ACCESS_ITEM_KEY_TYPE);
        }
    }
}
