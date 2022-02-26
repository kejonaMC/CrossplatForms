package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.Map;

@ConfigSerializable
public class InterfaceAction extends SimpleAction<String> {

    public InterfaceAction(@Nonnull String value) {
        super(value);
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler) {
        String resolved = CrossplatForms.getInstance().getPlaceholders().setPlaceholders(player, super.getValue(), additionalPlaceholders);
        Interface ui = interfaceManager.getInterface(resolved, bedrockHandler.isBedrockPlayer(player.getUuid()));
        if (ui == null) {
            Logger logger = Logger.getLogger();
            logger.severe("Attempted to make a player open a form or menu '" + resolved + "', but it does not exist. This is a configuration error!");
            if (logger.isDebug()) {
                Thread.dumpStack();
            }
        } else {
            ui.send(player, interfaceManager);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
