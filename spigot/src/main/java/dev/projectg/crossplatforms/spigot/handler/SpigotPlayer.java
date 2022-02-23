package dev.projectg.crossplatforms.spigot.handler;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.handler.FormPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotPlayer implements FormPlayer {

    private final Player handle;

    public SpigotPlayer(@Nonnull Player handle) {
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

    public Map<String, Boolean> getPermissions() {
        return handle.getEffectivePermissions()
                .stream()
                .filter(info -> info.getPermission().startsWith("crossplatforms"))
                .sorted(Comparator.comparing(PermissionAttachmentInfo::getPermission))
                .collect(Collectors.toMap(PermissionAttachmentInfo::getPermission, PermissionAttachmentInfo::getValue, (x, y) -> y, LinkedHashMap::new));
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
