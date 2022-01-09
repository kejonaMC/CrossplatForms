package dev.projectg.crossplatforms.handler.server;

import java.util.UUID;

public interface Player {

    /**
     * @return The player's UUID
     */
    UUID getUUID();

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
}
