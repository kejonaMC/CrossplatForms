package dev.projectg.crossplatforms.bungeecord.handler;

import dev.projectg.crossplatforms.bungeecord.CrossplatFormsBungeeCord;
import dev.projectg.crossplatforms.handler.FormPlayer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.TextComponent;
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
    public void sendRaw(TextComponent component) {
        player.sendMessage(CrossplatFormsBungeeCord.COMPONENT_SERIALIZER.serialize(component));
    }

    @Override
    public Object getHandle() {
        return player;
    }
}
