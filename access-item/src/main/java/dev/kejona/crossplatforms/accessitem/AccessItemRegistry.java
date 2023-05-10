package dev.kejona.crossplatforms.accessitem;


import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.permission.PermissionDefault;
import dev.kejona.crossplatforms.permission.Permissions;
import dev.kejona.crossplatforms.reloadable.Reloadable;
import dev.kejona.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AccessItemRegistry implements Reloadable {

    private final ConfigManager configManager;
    private final Permissions permissions;

    @Getter
    private final Map<String, AccessItem> items = new HashMap<>();

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

    public AccessItemRegistry(ConfigManager configManager, Permissions permissions) {
        this.configManager = configManager;
        this.permissions = permissions;
        ReloadableRegistry.register(this);
        load();
    }

    /**
     * Adds access items from the Access Items config.
     * Does not clear existing items.
     */
    private void load() {
        items.clear();

        if (!configManager.getConfig(AccessItemConfig.class).isPresent()) {
            enabled = false;
            return;
        }

        AccessItemConfig config = configManager.getConfig(AccessItemConfig.class).get();
        enabled = config.isEnable();
        if (enabled) {
            setHeldSlot = config.isSetHeldSlot();
            globalPermissionDefaults = config.getGlobalPermissionDefaults();

            Set<Permission> permissions = new HashSet<>();

            for (String identifier : config.getItems().keySet()) {
                AccessItem item = config.getItems().get(identifier);
                items.put(identifier, item);

                // Register permissions with the server
                item.generatePermissions(this);
                permissions.addAll(item.getPermissions().values());
            }

            this.permissions.registerPermissions(permissions);
        }
    }

    @Override
    public boolean reload() {
        load();
        return true;
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

    public abstract boolean giveAccessItem(FormPlayer player, AccessItem accessItem, boolean setHeldSlot);
}
