package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.permission.PermissionDefault;
import dev.projectg.crossplatforms.permission.Permission;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * An abstract handle on the server implementation
 */
public interface ServerHandler {

    /**
     * Get a player by their UUID
     */
    @Nullable
    Player getPlayer(UUID uuid);

    /**
     * Get a player by the name
     */
    @Nullable
    Player getPlayer(String name);

    List<Player> getPlayers();

    /**
     * @return True if the plugin identified by the given ID is enabled
     */
    boolean isPluginEnabled(String id);

    boolean isPermissionRegistered(String key);

    void registerPermission(String key, @Nullable String description, PermissionDefault def);

    default void registerPermission(Permission permission) {
        registerPermission(permission.key(), permission.description(), permission.defaultPermission());
    }

    void unregisterPermission(String key);

    /**
     * Execute a command as the server console
     * @param command The command string to execute
     */
    void dispatchCommand(String command);

    /**
     * Execute a command as the given player.
     * @param player The player to run the command as
     * @param command The command string to execute
     */
    void dispatchCommand(UUID player, String command);
}
