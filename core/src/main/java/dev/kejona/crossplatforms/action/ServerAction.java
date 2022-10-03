package dev.kejona.crossplatforms.action;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;

@ConfigSerializable
public class ServerAction implements GenericAction {

    private static final String TYPE = "server";

    @Required
    private String server;

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver) {
        String server = resolver.apply(this.server);
        if (!player.switchBackendServer(server)) {
            Logger.get().warn("Server '" + server + "' does not exist! Not transferring " + player.getName());
            player.sendMessage(Component.text("Server ", NamedTextColor.RED)
                    .append(Component.text(server))
                    .append(Component.text(" doesn't exist.", NamedTextColor.RED)));
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public boolean serializeWithType() {
        return false; // can infer based off server node
    }

    public static void register(ActionSerializer serializer) {
        serializer.register(TYPE, ServerAction.class, typeResolver());
    }

    private static TypeResolver typeResolver() {
        return node -> {
            if (node.node("server").getString() != null) {
                return TYPE;
            } else {
                return null;
            }
        };
    }
}
