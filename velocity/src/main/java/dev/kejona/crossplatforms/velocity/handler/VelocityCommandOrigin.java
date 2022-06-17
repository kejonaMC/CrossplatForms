package dev.kejona.crossplatforms.velocity.handler;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import dev.kejona.crossplatforms.command.CommandOrigin;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.TextComponent;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class VelocityCommandOrigin implements CommandOrigin {

    @Nonnull
    private final CommandSource source;

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public void sendRaw(TextComponent message) {
        source.sendMessage(message);
    }

    @Override
    public boolean isPlayer() {
        return source instanceof Player;
    }

    @Override
    public boolean isConsole() {
        return source instanceof ConsoleCommandSource;
    }

    @Override
    public Object getHandle() {
        return source;
    }

    @Override
    public Optional<UUID> getUUID() {
        if (source instanceof Player) {
            return Optional.of(((Player) source).getUniqueId());
        } else {
            return Optional.empty();
        }
    }
}
