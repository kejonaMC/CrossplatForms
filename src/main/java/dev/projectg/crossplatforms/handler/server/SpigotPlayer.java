package dev.projectg.crossplatforms.handler.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
public class SpigotPlayer implements dev.projectg.crossplatforms.handler.server.Player {

    @Getter
    private final Player handle;

    @Override
    public UUID getUUID() {
        return handle.getUniqueId();
    }

    @Override
    public String getName() {
        return handle.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }
}
