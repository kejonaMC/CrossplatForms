package dev.projectg.crossplatforms.config.mapping.bedrock.simple;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.mapping.bedrock.BedrockForm;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.Getter;
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

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleForm extends BedrockForm {

    private String title = "";
    private String content = "";
    private List<FormButton> buttons = Collections.emptyList();

    public void sendForm(@Nonnull UUID uuid) {
        Logger logger = Logger.getLogger();

        Player player = Bukkit.getServer().getPlayer(uuid);
        if (player == null) {
            logger.severe("Unable to find a Bukkit Player for the given UUID: " + uuid);
            return;
        }

        BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
        if (!bedrockHandler.isBedrockPlayer(uuid)) {
            logger.severe("Player with UUID " + uuid + " is not a Bedrock Player!");
        }

        // Resolve any placeholders in the button text
        List<FormButton> formattedButtons = new ArrayList<>();
        for (FormButton rawButton : buttons) {
            FormButton copiedButton = rawButton.withText(PlaceholderUtils.setPlaceholders(player, rawButton.getText()));
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

            FormButton button = formattedButtons.get(response.getClickedButtonId());

            // Handle effects of pressing the button
            InterfaceUtils.affectPlayer(button, player);
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }
}
