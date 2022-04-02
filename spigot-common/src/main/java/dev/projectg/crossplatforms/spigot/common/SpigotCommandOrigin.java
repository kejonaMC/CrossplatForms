package dev.projectg.crossplatforms.spigot.common;

import dev.projectg.crossplatforms.command.CommandOrigin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class SpigotCommandOrigin implements CommandOrigin {

    @Nonnull
    @Getter
    private final CommandSender handle;

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }

    @Override
    public void sendRaw(TextComponent message) {
        handle.sendMessage(SpigotCommon.LEGACY_SERIALIZER.serialize(message));
    }

    @Override
    public boolean isConsole() {
        return handle instanceof ConsoleCommandSender;
    }

    @Override
    public boolean isPlayer() {
        return handle instanceof Player;
    }

    @Override
    public Optional<UUID> getUUID() {
        if (isPlayer()) {
            return Optional.of(((Player) handle).getUniqueId());
        } else {
            return Optional.empty();
        }
    }
}
