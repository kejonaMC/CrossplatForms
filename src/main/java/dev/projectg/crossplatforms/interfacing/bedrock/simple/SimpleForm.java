package dev.projectg.crossplatforms.interfacing.bedrock.simple;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.ToString;
import org.bukkit.entity.Player;
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
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class SimpleForm extends BedrockForm {

    private String content = "";
    private List<SimpleButton> buttons = Collections.emptyList();

    @Override
    public void send(@Nonnull dev.projectg.crossplatforms.handler.Player recipient) {
        InterfaceManager registry = CrossplatForms.getInstance().getInterfaceManager();
        Logger logger = Logger.getLogger();
        UUID uuid = recipient.getUuid();
        Player player = (Player) recipient.getHandle();

        BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
        if (!bedrockHandler.isBedrockPlayer(uuid)) {
            logger.severe("Player with UUID " + uuid + " is not a Bedrock Player!");
            return;
        }

        // Resolve any placeholders in the button text
        List<SimpleButton> formattedButtons = new ArrayList<>();
        for (SimpleButton rawButton : buttons) {
            SimpleButton copiedButton = rawButton.withText(PlaceholderUtils.setPlaceholders(player, rawButton.getText()));
            formattedButtons.add(copiedButton);
        }

        // Create the form
        @SuppressWarnings("unchecked")
        org.geysermc.cumulus.SimpleForm form = org.geysermc.cumulus.SimpleForm.of(
                PlaceholderUtils.setPlaceholders(player, super.getTitle()),
                PlaceholderUtils.setPlaceholders(player, content),
                (List<ButtonComponent>)(List<?>) formattedButtons); // sad noises

        // Set the response handler
        form.setResponseHandler((responseData) -> {
            logger.debug("Parsing form response for form " + super.getIdentifier() + " and player: " + player.getName());
            SimpleFormResponse response = form.parseResponse(responseData);
            if (response.isClosed()) {
                return;
            } else if (response.isInvalid()) {
                if (logger.isDebug()) {
                    logger.warn("Got invalid response for form " + super.getIdentifier() + " by player " + player.getName());
                    logger.warn(form.getJsonData());
                }
                return;
            }

            SimpleButton button = formattedButtons.get(response.getClickedButtonId());

            // Handle effects of pressing the button
            button.affectPlayer(registry, player);
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }
}
