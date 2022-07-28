package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Map;

@ConfigSerializable
public class ServerAction implements Action {

    private static final String TYPE = "server";

    private final transient Placeholders placeholders;

    @Required
    private String server;

    @Inject
    private ServerAction(Placeholders placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@NotNull FormPlayer player, @NotNull Map<String, String> additionalPlaceholders) {
        String server = placeholders.setPlaceholders(player, this.server, additionalPlaceholders);
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
        serializer.genericAction(TYPE, ServerAction.class, typeResolver());
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
