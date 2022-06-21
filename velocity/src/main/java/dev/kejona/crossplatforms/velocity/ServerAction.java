package dev.kejona.crossplatforms.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.action.SimpleAction;
import dev.kejona.crossplatforms.handler.FormPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.util.Map;

public class ServerAction extends SimpleAction<String> {

    public static final String TYPE = "server";
    private static final ProxyServer PROXY = CrossplatFormsVelocity.getInstance().getServer();

    @Inject
    public ServerAction(String value) {
        super(TYPE, value);
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer formPlayer, @Nonnull Map<String, String> additionalPlaceholders) {
        Player player = (Player) formPlayer.getHandle();
        String serverName = value();
        RegisteredServer server = PROXY.getServer(serverName).orElse(null);
        if (server == null) {
            Logger.get().warn("Server '" + serverName + "' does not exist! Not transferring " + formPlayer.getName());
            player.sendMessage(Component.text("Server ", NamedTextColor.RED)
                    .append(Component.text(serverName))
                    .append(Component.text(" doesn't exist.", NamedTextColor.RED)));
        } else {
            player.createConnectionRequest(server).connectWithIndication();
        }
    }
}
