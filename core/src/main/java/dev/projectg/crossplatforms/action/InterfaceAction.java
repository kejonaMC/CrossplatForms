package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Map;

@ToString
@ConfigSerializable
public class InterfaceAction implements Action {

    @Required
    String form = null;

    @Override
    public void affectPlayer(@NotNull FormPlayer player, @NotNull Map<String, String> additionalPlaceholders, @NotNull InterfaceManager interfaceManager, @NotNull BedrockHandler bedrockHandler) {
        String resolved = CrossplatForms.getInstance().getPlaceholders().setPlaceholders(player, form, additionalPlaceholders);
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
}
