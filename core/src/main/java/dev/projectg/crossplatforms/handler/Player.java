package dev.projectg.crossplatforms.handler;

import java.util.Map;
import java.util.UUID;

public interface Player {

    /**
     * @return The player's UUID
     */
    UUID getUuid();

    /**
     * @return The player's name
     */
    String getName();

    /**
     * Check the player has a permission
     * @param permission the permission to check
     * @return True if the player has the permission
     */
    boolean hasPermission(String permission);

    /**
     * @return A Map of registered permission key -> boolean value of this player, only for permissions
     * from CrossplatForms.
     */
    Map<String, Boolean> getPermissions();

    void sendMessage(String message);

    Object getHandle();
}
