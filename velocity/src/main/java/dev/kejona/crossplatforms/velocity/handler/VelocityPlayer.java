package dev.projectg.crossplatforms.velocity.handler;

import com.velocitypowered.api.proxy.Player;
import dev.projectg.crossplatforms.handler.FormPlayer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.TextComponent;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class VelocityPlayer implements FormPlayer {

    @Nonnull
    private final Player player;

    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }

    @Override
    public String getName() {
        return player.getUsername();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public void sendRaw(TextComponent component) {
        player.sendMessage(component);
    }

    @Override
    public Object getHandle() {
        return player;
    }
}
