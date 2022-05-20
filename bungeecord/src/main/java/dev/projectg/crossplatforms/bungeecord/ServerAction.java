package dev.projectg.crossplatforms.bungeecord;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.SimpleAction;
import dev.projectg.crossplatforms.handler.FormPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class ServerAction extends SimpleAction<String> {

    public static final String TYPE = "server";

    public ServerAction(String value) {
        super(TYPE, value);
    }

    @Inject
    private ServerAction() {
        this("");
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer formPlayer, @Nonnull Map<String, String> additionalPlaceholders) {
        ProxiedPlayer player = (ProxiedPlayer) formPlayer.getHandle();
        String serverName = value();
        ServerInfo downstream = ProxyServer.getInstance().getServerInfo(serverName); // find target
        if (downstream == null) {
            Logger.getLogger().warn("Server '" + serverName + "' does not exist!");
            TextComponent start = new TextComponent("Server ");
            start.setColor(ChatColor.RED);
            TextComponent middle = new TextComponent(serverName);
            TextComponent end = new TextComponent(" doesn't exist.");
            end.setColor(ChatColor.RED);
            player.sendMessage(new TextComponent(start, middle, end));
        } else {
            player.connect(downstream, ServerConnectEvent.Reason.PLUGIN);
        }
    }
}
