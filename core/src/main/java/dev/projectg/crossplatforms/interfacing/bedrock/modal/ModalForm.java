package dev.projectg.crossplatforms.interfacing.bedrock.modal;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import lombok.ToString;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@ToString
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ModalForm extends BedrockForm {

    private String content = "";

    @Required
    private ModalButton button1 = null;

    @Required
    private ModalButton button2 = null;

    @Override
    public void send(@Nonnull FormPlayer player, @Nonnull InterfaceManager interfaceManager) {
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        Logger logger = Logger.getLogger();
        UUID uuid = player.getUuid();

        BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
        if (!bedrockHandler.isBedrockPlayer(uuid)) {
            logger.severe("Player with UUID " + uuid + " is not a Bedrock Player!");
            return;
        }

        org.geysermc.cumulus.ModalForm form = org.geysermc.cumulus.ModalForm.of(
                placeholders.setPlaceholders(player, super.getTitle()),
                placeholders.setPlaceholders(player, content),
                placeholders.setPlaceholders(player, button1.getText()),
                placeholders.setPlaceholders(player, button2.getText()));

        // Set the response handler
        form.setResponseHandler((responseData) -> {
            ModalFormResponse response = form.parseResponse(responseData);
            if (response.isClosed()) {
                return;
            } else if (response.isInvalid()) {
                if (logger.isDebug()) {
                    logger.warn("Got invalid response for form " + super.getIdentifier() + " by player " + player.getName());
                    logger.warn(form.getJsonData());
                }
                return;
            }
            logger.debug("Parsing form response for form " + super.getIdentifier() + " and player: " + player.getName());
            int id = response.getClickedButtonId();
            List<Action> actions = switch (id) {
                case 0 -> button1.getActions();
                case 1 -> button2.getActions();
                default -> throw new AssertionError();
            };

            // Handle effects of pressing the button
            for (Action action : actions) {
                action.affectPlayer(player, interfaceManager, bedrockHandler);
            }
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }
}
