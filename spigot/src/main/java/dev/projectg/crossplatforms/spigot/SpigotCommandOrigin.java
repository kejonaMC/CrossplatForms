package dev.projectg.crossplatforms.spigot;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.command.CommandOrigin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class SpigotCommandOrigin implements CommandOrigin {

    @Getter
    CommandSender handle;

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }

    @Override
    public void sendRaw(String message) {
        handle.sendMessage(message);
    }

    @Override
    public void sendMessage(String message) {
        handle.sendMessage(Constants.MESSAGE_PREFIX + message);
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
