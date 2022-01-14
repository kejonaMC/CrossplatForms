package dev.projectg.crossplatforms.handler;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class SpigotPlayer implements Player {

    private final org.bukkit.entity.Player handle;

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
