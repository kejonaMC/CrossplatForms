package dev.kejona.crossplatforms.bungeecord.handler;

import dev.kejona.crossplatforms.bungeecord.CrossplatFormsBungeeCord;
import dev.kejona.crossplatforms.command.CommandOrigin;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.CommandSender;
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
    public void sendRaw(TextComponent message) {
        sender.sendMessage(CrossplatFormsBungeeCord.COMPONENT_SERIALIZER.serialize(message));
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
