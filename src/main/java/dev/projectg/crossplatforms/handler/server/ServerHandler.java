package dev.projectg.crossplatforms.handler.server;

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
}
