package dev.projectg.crossplatforms.interfacing.bedrock.simple;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import lombok.ToString;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ToString
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleForm extends BedrockForm {

    private String content = "";
    private List<SimpleButton> buttons = Collections.emptyList();

    @Override
    public void send(@Nonnull FormPlayer player) {
        InterfaceManager registry = CrossplatForms.getInstance().getInterfaceManager();
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        Logger logger = Logger.getLogger();
        UUID uuid = player.getUuid();

        BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
        if (!bedrockHandler.isBedrockPlayer(uuid)) {
            logger.severe("Player with UUID " + uuid + " is not a Bedrock Player!");
            return;
        }

        // Resolve any placeholders in the button text
        List<SimpleButton> formattedButtons = new ArrayList<>();
        for (SimpleButton rawButton : buttons) {
            SimpleButton copiedButton = rawButton.withText(placeholders.setPlaceholders(player, rawButton.getText()));
            formattedButtons.add(copiedButton);
        }

        // Create the form
        @SuppressWarnings("unchecked")
        org.geysermc.cumulus.SimpleForm form = org.geysermc.cumulus.SimpleForm.of(
                placeholders.setPlaceholders(player, super.getTitle()),
                placeholders.setPlaceholders(player, content),
                (List<ButtonComponent>)(List<?>) formattedButtons); // sad noises

        // Set the response handler
        form.setResponseHandler((responseData) -> {
            SimpleFormResponse response = form.parseResponse(responseData);
            if (response.isClosed()) {
                return;
            } else if (response.isInvalid()) {
                if (logger.isDebug()) {
                    logger.warn("Got invalid response for form " + super.getIdentifier() + " by player: " + player.getName());
                    logger.warn(form.getJsonData());
                }
                return;
            }
            logger.debug("Parsing form response for form " + super.getIdentifier() + " and player: " + player.getName());
            // Handle effects of pressing the button
            List<Action> actions = formattedButtons.get(response.getClickedButtonId()).getActions();
            for (Action action : actions) {
                action.affectPlayer(player, registry, bedrockHandler);
            }
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }
}