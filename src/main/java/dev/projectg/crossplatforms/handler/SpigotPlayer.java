package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.Constants;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class SpigotPlayer implements Player {

    private final org.bukkit.entity.Player handle;

    @Override
    public UUID getUuid() {
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

    @Override
    public void sendMessage(String message) {
        handle.sendMessage(Constants.MESSAGE_PREFIX + message);
    }

    @Override
    public Object getHandle() {
        return handle;
    }
}
