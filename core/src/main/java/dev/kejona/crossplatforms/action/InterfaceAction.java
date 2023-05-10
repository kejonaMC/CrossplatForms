package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.ArgumentException;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class InterfaceAction implements GenericAction {

    private static final String TYPE = "form";
    private static final Logger LOGGER = Logger.get();

    @Required
    private String form;

    private Map<String, String> arguments = Collections.emptyMap();

    private final transient BedrockHandler bedrockHandler;
    private final transient Interfacer interfacer;

    @Inject
    public InterfaceAction(BedrockHandler bedrockHandler, Interfacer interfacer) {
        this.bedrockHandler = bedrockHandler;
        this.interfacer = interfacer;
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

        Map<String, String> arguments = new HashMap<>();
        for (Map.Entry<String, String> e : this.arguments.entrySet()) {
            arguments.put(e.getKey(), resolver.apply(e.getValue()));
        }

        try {
            // The resolver given to this action is not passed to the form being opened.
            // If this action is triggered by a form that has arguments, and the user wants those arguments to be passed
            // to the form that this is opening, they must be passed explicitly as arguments (not through the resolver)
            ui.send(player, arguments);
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
