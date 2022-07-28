package dev.kejona.crossplatforms.velocity.handler;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.velocity.CrossplatFormsVelocity;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import java.util.UUID;

@AllArgsConstructor
public class VelocityPlayer implements FormPlayer {

    private static final ProxyServer PROXY = CrossplatFormsVelocity.getInstance().getServer();

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
    public void sendRaw(Component component) {
        player.sendMessage(component);
    }

    @Override
    public boolean switchBackendServer(String serverName) {
        RegisteredServer server = PROXY.getServer(serverName).orElse(null);
        if (server == null) {
            return false;
        }
        player.createConnectionRequest(server).connectWithIndication();
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHandle(Class<T> asType) throws ClassCastException {
        return (T) player;
    }
}
