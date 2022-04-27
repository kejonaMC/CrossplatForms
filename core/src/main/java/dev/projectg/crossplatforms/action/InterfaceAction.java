package dev.projectg.crossplatforms.action;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Map;

@ConfigSerializable
public class InterfaceAction extends SimpleAction<String> {

    @Inject
    private transient BedrockHandler bedrockHandler;

    @Inject
    private transient InterfaceManager interfaceManager;

    @Inject
    private transient PlaceholderHandler placeholders;

    public static final String TYPE = "form";

    @Inject
    public InterfaceAction(@Nonnull String value) {
        super(TYPE, value);
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        String resolved = placeholders.setPlaceholders(player, value(), additionalPlaceholders);
        Interface ui = interfaceManager.getInterface(resolved, bedrockHandler.isBedrockPlayer(player.getUuid()));
        if (ui == null) {
            Logger logger = Logger.getLogger();
            logger.severe("Attempted to make a player open a form or menu '" + resolved + "', but it does not exist. This is a configuration error!");
            if (logger.isDebug()) {
                Thread.dumpStack();
            }
        } else {
            ui.send(player);
        }
    }
}
