package dev.kejona.crossplatforms.interfacing.bedrock.modal;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import lombok.ToString;
import org.geysermc.cumulus.form.ModalForm;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@ToString(callSuper = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ModalBedrockForm extends BedrockForm {

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
    public void send(@Nonnull FormPlayer player) {
        Logger logger = Logger.get();
        UUID uuid = player.getUuid();

        if (!bedrockHandler.isBedrockPlayer(uuid)) {
            logger.severe("Player with UUID " + uuid + " is not a Bedrock Player!");
            return;
        }

        ModalForm form = ModalForm.builder()
            .title(placeholders.setPlaceholders(player, getTitle()))
            .content(placeholders.setPlaceholders(player, content))
            .button1(placeholders.setPlaceholders(player, button1.getText()))
            .button2(placeholders.setPlaceholders(player, button2.getText()))
            .closedOrInvalidResultHandler(() -> handleIncorrect(player))
            .validResultHandler(response -> executeHandler(() -> {
                int id = response.clickedButtonId();
                List<Action> actions;
                if (id == 0) {
                    actions = button1.getActions();
                } else if (id == 1) {
                    actions = button2.getActions();
                } else {
                    throw new AssertionError("Got " + id + " from modal form response instead of 0 or 1");
                }

                // Handle effects of pressing the button
                Action.affectPlayer(player, actions);
            }))
            .build();

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }
}
