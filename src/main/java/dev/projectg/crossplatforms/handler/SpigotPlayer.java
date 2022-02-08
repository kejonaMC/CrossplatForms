package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.Constants;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

public class SpigotPlayer implements Player {

    private final org.bukkit.entity.Player handle;

    public SpigotPlayer(@Nonnull org.bukkit.entity.Player handle) {
        this.handle = Objects.requireNonNull(handle);
    }

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
