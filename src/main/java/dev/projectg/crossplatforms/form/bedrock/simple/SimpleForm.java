package dev.projectg.crossplatforms.form.bedrock.simple;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.form.bedrock.BedrockForm;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class SimpleForm extends BedrockForm {

    private String title = "";
    private String content = "";
    private List<SimpleButton> buttons = Collections.emptyList();

    public void sendForm(@Nonnull UUID bedrockPlayer) {
        Logger logger = Logger.getLogger();

        Player player = Bukkit.getServer().getPlayer(bedrockPlayer);
        if (player == null) {
            logger.severe("Unable to find a Bukkit Player for the given UUID: " + bedrockPlayer);
            return;
        }

        BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
        if (!bedrockHandler.isBedrockPlayer(bedrockPlayer)) {
            logger.severe("Player with UUID " + bedrockPlayer + " is not a Bedrock Player!");
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
                PlaceholderUtils.setPlaceholders(player, title),
                PlaceholderUtils.setPlaceholders(player, content),
                (List<ButtonComponent>)(List<?>) formattedButtons); // sad noises

        // Set the response handler
        form.setResponseHandler((responseData) -> {
            SimpleFormResponse response = form.parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            SimpleButton button = formattedButtons.get(response.getClickedButtonId());

            // Handle effects of pressing the button
            InterfaceUtils.affectPlayer(button, player);
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(bedrockPlayer, form);
    }
}
