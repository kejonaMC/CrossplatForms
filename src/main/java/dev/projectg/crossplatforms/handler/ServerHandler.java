package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.permission.PermissionDefault;
import dev.projectg.crossplatforms.permission.Permission;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

/**
 * An abstract handle on the server implementation
 */
public interface ServerHandler {

    /**
     * Get a player by their UUID
     */
    Player getPlayer(UUID uuid);

    /**
     * Get a player by the name
     */
    Player getPlayer(String name);

    /**
     * @return True if the plugin identified by the given ID is enabled
     */
    boolean isPluginEnabled(String id);

    void registerPermission(String key, @Nullable String description, PermissionDefault def);

    default void registerPermission(Permission permission) {
        registerPermission(permission.key(), permission.description(), permission.defaultPermission());
    }

    void unregisterPermission(String key);
}
