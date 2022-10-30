package dev.kejona.crossplatforms.bungeecord.handler;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.bungeecord.CrossplatFormsBungeeCord;
import dev.kejona.crossplatforms.handler.FormPlayer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@AllArgsConstructor
public class BungeeCordPlayer implements FormPlayer {

    private static final ProxyServer PROXY = ProxyServer.getInstance();

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

    @Nullable
    @Override
    public String getEncodedSkinData() {
        InitialHandler handler;
        try {
            handler = (InitialHandler) player.getPendingConnection();
        } catch (ClassCastException e) {
            Logger.get().warn("Incompatible BungeeCord fork, unable to get skin texture");
            if (Logger.get().isDebug()) {
                e.printStackTrace();
            }
            return null;
        }

        LoginResult loginResult = handler.getLoginProfile();
        if (loginResult == null) {
            return null;
        }

        for (Property property : loginResult.getProperties()) {
            if (property.getName().equals("textures")) {
                String value = property.getValue();
                if (!value.isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public void sendRaw(Component component) {
        player.sendMessage(CrossplatFormsBungeeCord.COMPONENT_SERIALIZER.serialize(component));
    }

    @Override
    public boolean switchBackendServer(String server) {
        ServerInfo downstream = PROXY.getServerInfo(server); // find target
        if (downstream == null) {
            return false;
        }
        player.connect(downstream, ServerConnectEvent.Reason.PLUGIN);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHandle(Class<T> asType) throws ClassCastException {
        return (T) player;
    }
}
