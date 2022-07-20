package dev.kejona.crossplatforms.bungeecord.handler;

import dev.kejona.crossplatforms.bungeecord.CrossplatFormsBungeeCord;
import dev.kejona.crossplatforms.handler.FormPlayer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class BungeeCordPlayer implements FormPlayer {

    @Nonnull
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
    public void sendRaw(Component component) {
        player.sendMessage(CrossplatFormsBungeeCord.COMPONENT_SERIALIZER.serialize(component));
    }

    @Override
    public Object getHandle() {
        return player;
    }
}
