package dev.projectg.crossplatforms.velocity.handler;

import com.velocitypowered.api.proxy.Player;
import dev.projectg.crossplatforms.handler.FormPlayer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
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
    public Map<String, Boolean> getPermissions() {
        return Collections.emptyMap(); // todo: find way to do this with velocity or a third party plugin
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(Component.text(message));
    }

    @Override
    public Object getHandle() {
        return player;
    }
}
