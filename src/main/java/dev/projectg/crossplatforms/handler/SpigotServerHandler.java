package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.permission.DefaultPermission;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    public boolean isPluginEnabled(String id) {
        return server.getPluginManager().isPluginEnabled(id);
    }

    @Override
    public void registerPermission(String key, @Nullable String description, DefaultPermission def) {
        PermissionDefault perm = switch (def) {
            case TRUE -> PermissionDefault.TRUE;
            case FALSE -> PermissionDefault.FALSE;
            case OP -> PermissionDefault.OP;
        };

        server.getPluginManager().addPermission(new Permission(key, description, perm));
    }

    @Override
    public void unregisterPermission(String key) {
        server.getPluginManager().removePermission(new Permission(key));
    }
}
