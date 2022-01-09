package dev.projectg.crossplatforms.handler.server;

import lombok.RequiredArgsConstructor;
import org.bukkit.Server;

import java.util.UUID;

@RequiredArgsConstructor
public class SpigotServerHandler implements ServerHandler {

    private final Server server;

    @Override
    public Player getPlayer(UUID uuid) {
        return new SpigotPlayer(server.getPlayer(uuid));
    }

    @Override
    public Player getPlayer(String name) {
        return new SpigotPlayer(server.getPlayer(name));
    }

    @Override
    public boolean isPluginEnabled(String name) {
        return server.getPluginManager().isPluginEnabled(name);
    }
}
