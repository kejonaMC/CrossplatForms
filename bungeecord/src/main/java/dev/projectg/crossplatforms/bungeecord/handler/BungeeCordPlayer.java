package dev.projectg.crossplatforms.bungeecord.handler;

import dev.projectg.crossplatforms.handler.FormPlayer;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BungeeCordPlayer implements FormPlayer {

    private final ProxiedPlayer player;

    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public Map<String, Boolean> getPermissions() {
        // getPermissions() returns string collection, of all permissions granted
        return player.getPermissions().stream().collect(Collectors.toMap(Function.identity(), e -> true));
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(TextComponent.fromLegacyText(message));

    }

    @Override
    public Object getHandle() {
        return player;
    }
}
