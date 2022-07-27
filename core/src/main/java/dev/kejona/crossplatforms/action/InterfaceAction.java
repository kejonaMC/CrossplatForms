package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.Map;

@ConfigSerializable
public class InterfaceAction implements Action {

    private static final String TYPE = "form";
    private static final Logger LOGGER = Logger.get();

    private final transient BedrockHandler bedrockHandler;
    private final transient Interfacer interfacer;
    private final transient Placeholders placeholders;

    @Required
    private String form;

    @Inject
    public InterfaceAction(BedrockHandler bedrockHandler, Interfacer interfacer, Placeholders placeholders) {
        this.bedrockHandler = bedrockHandler;
        this.interfacer = interfacer;
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        String resolved = placeholders.setPlaceholders(player, form, additionalPlaceholders);
        Interface ui = interfacer.getInterface(resolved, bedrockHandler.isBedrockPlayer(player.getUuid()));
        if (ui == null) {
            LOGGER.severe("Attempted to make a player open a form or menu '" + resolved + "', but it does not exist. This is a configuration error!");
            return;
        }

        String permission = ui.permission(Interface.Limit.USE);
        if (player.hasPermission(permission)) {
            ui.send(player);
        } else {
            LOGGER.severe("Attempted to make a player open a form or menu '" + resolved + "', but they do not have the following permission: " + permission);
            player.warn("You don't have permission to open: " + resolved);
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    public static void register(ActionSerializer serializer) {
        serializer.genericAction(TYPE, InterfaceAction.class, typeResolver());
    }

    private static TypeResolver typeResolver() {
        return node -> {
            if (node.node("form").getString() != null) {
                return TYPE;
            } else {
                return null;
            }
        };
    }
}
