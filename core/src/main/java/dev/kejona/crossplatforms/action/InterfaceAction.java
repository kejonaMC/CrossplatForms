package dev.projectg.crossplatforms.action;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.Placeholders;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.Interfacer;

import javax.annotation.Nonnull;
import java.util.Map;

public class InterfaceAction extends SimpleAction<String> {

    public static final String TYPE = "form";

    private final transient BedrockHandler bedrockHandler;
    private final transient Interfacer interfacer;
    private final transient Placeholders placeholders;

    @Inject
    public InterfaceAction(String value,
                           BedrockHandler bedrockHandler,
                           Interfacer interfacer,
                           Placeholders placeholders) {
        super(TYPE, value);
        this.bedrockHandler = bedrockHandler;
        this.interfacer = interfacer;
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        String resolved = placeholders.setPlaceholders(player, value(), additionalPlaceholders);
        Interface ui = interfacer.getInterface(resolved, bedrockHandler.isBedrockPlayer(player.getUuid()));
        if (ui == null) {
            Logger logger = Logger.get();
            logger.severe("Attempted to make a player open a form or menu '" + resolved + "', but it does not exist. This is a configuration error!");
        } else {
            ui.send(player);
        }
    }
}
