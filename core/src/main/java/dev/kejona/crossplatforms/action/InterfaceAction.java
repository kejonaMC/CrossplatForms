package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.interfacing.ArgumentException;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class InterfaceAction implements GenericAction {

    private static final String TYPE = "form";
    private static final Logger LOGGER = Logger.get();

    @Required
    private String form;

    private Map<String, String> args = Collections.emptyMap();

    private final transient BedrockHandler bedrockHandler;
    private final transient Interfacer interfacer;
    private final transient Placeholders placeholders;

    @Inject
    public InterfaceAction(BedrockHandler bedrockHandler, Interfacer interfacer, Placeholders placeholders) {
        this.bedrockHandler = bedrockHandler;
        this.interfacer = interfacer;
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver) {
        String form = resolver.apply(this.form);
        Interface ui = interfacer.getInterface(form, bedrockHandler.isBedrockPlayer(player.getUuid()));
        if (ui == null) {
            LOGGER.severe("Attempted to make a player open a form or menu '" + form + "', but it does not exist. This is a configuration error!");
            return;
        }

        String permission = ui.permission(Interface.Limit.USE);
        if (!player.hasPermission(permission)) {
            LOGGER.severe("Attempted to make a player open a form or menu '" + form + "', but they do not have the following permission: " + permission);
            player.warn("You don't have permission to open: " + form);
            return;
        }

        try {
            ui.send(player, placeholders.resolver(player), args);
        } catch (ArgumentException e) {
            player.warn("A configuration error resulted in you not opening a form or menu.");
            Logger.get().severe("Failed to open '" + form + "' because: " + e.getMessage());
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public boolean serializeWithType() {
        return false; // can infer based off form node
    }

    public static void register(ActionSerializer serializer) {
        serializer.register(TYPE, InterfaceAction.class, typeResolver());
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
