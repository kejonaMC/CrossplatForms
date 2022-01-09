package dev.projectg.crossplatforms.handler.server;

import java.util.UUID;

public interface ServerHandler {

    Player getPlayer(UUID uuid);
    Player getPlayer(String name);

    boolean isPluginEnabled(String name);
}
