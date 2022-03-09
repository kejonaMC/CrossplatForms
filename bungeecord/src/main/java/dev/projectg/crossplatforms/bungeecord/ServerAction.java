package dev.projectg.crossplatforms.bungeecord;

import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ServerAction extends SimpleAction<String> {

    public static final String IDENTIFIER = "server";

    public ServerAction(@NotNull String value) {
        super(value);
    }

    @Override
    public void affectPlayer(@NotNull FormPlayer formPlayer, @NotNull Map<String, String> additionalPlaceholders, @NotNull InterfaceManager interfaceManager, @NotNull BedrockHandler bedrockHandler) {
        ProxiedPlayer player = (ProxiedPlayer) formPlayer.getHandle();
        ServerInfo downstream = ProxyServer.getInstance().getServerInfo(value()); // find target
        player.connect(downstream, ServerConnectEvent.Reason.PLUGIN);
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }
}
