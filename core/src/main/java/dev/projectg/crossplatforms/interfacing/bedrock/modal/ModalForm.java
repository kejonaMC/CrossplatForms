package dev.projectg.crossplatforms.interfacing.bedrock.modal;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import lombok.ToString;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@ToString(callSuper = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ModalForm extends BedrockForm {

    public static final String TYPE = "modal_form";

    private String content = "";

    @Required
    private ModalButton button1 = null;

    @Required
    private ModalButton button2 = null;

    @Override
    public String type() {
        return TYPE;
    }

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
        Consumer<String> handler = (responseData) -> {
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

            List<Action> actions;
            if (id == 0) {
                actions = button1.getActions();
            } else if (id == 1) {
                actions = button2.getActions();
            } else {
                throw new AssertionError("Got " + id + " from modal form response instead of 0 or 1");
            }

            // Handle effects of pressing the button
            Action.affectPlayer(player, actions, interfaceManager, bedrockHandler);
        };

        setResponseHandler(form, handler, interfaceManager.getServerHandler(), bedrockHandler);

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }
}
