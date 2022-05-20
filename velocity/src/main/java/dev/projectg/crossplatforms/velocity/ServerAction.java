package dev.projectg.crossplatforms.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.handler.FormPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.util.Map;

public class ServerAction extends SimpleAction<String> {

    public static final String IDENTIFIER = "server";
    private static final ProxyServer PROXY = CrossplatFormsVelocity.getInstance().getServer();

    @Inject
    public ServerAction(String value) {
        super(IDENTIFIER, value);
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer formPlayer, @Nonnull Map<String, String> additionalPlaceholders) {
        Player player = (Player) formPlayer.getHandle();
        String serverName = value();
        RegisteredServer server = PROXY.getServer(serverName).orElse(null);
        if (server == null) {
            Logger.getLogger().warn("Server '" + serverName + "' does not exist! Not transferring " + formPlayer.getName());
            player.sendMessage(Component.text("Server ", NamedTextColor.RED)
                    .append(Component.text(serverName))
                    .append(Component.text(" doesn't exist.", NamedTextColor.RED)));
        } else {
            player.createConnectionRequest(server).connectWithIndication();
        }
    }
}
