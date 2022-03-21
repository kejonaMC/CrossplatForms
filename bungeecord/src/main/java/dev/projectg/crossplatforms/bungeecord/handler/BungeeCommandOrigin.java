package dev.projectg.crossplatforms.bungeecord.handler;

import dev.projectg.crossplatforms.command.CommandOrigin;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class BungeeCommandOrigin implements CommandOrigin {

    @Nonnull
    private final CommandSender sender;

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public void sendRaw(String message) {
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof ProxiedPlayer;
    }

    @Override
    public boolean isConsole() {
        return !(sender instanceof ProxiedPlayer);
    }

    @Override
    public Object getHandle() {
        return sender;
    }

    @Override
    public Optional<UUID> getUUID() {
        if (sender instanceof ProxiedPlayer) {
            return Optional.of(((ProxiedPlayer) sender).getUniqueId());
        } else {
            return Optional.empty();
        }
    }
}
