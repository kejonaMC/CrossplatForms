package dev.projectg.crossplatforms.spigot.common;

import dev.projectg.crossplatforms.handler.FormPlayer;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

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

    @Override
    public void sendRaw(TextComponent component) {
        handle.sendMessage(SpigotCommon.LEGACY_SERIALIZER.serialize(component));
    }

    @Override
    public Object getHandle() {
        return handle;
    }
}
