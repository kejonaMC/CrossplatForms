package dev.projectg.crossplatforms.form.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.form.ClickAction;
import dev.projectg.crossplatforms.form.bedrock.BedrockForm;
import dev.projectg.crossplatforms.handler.bedrock.BedrockHandler;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class CustomForm extends BedrockForm {

    private FormImage image = null;
    private List<CustomComponent> components = Collections.emptyList();

    @Required
    private ClickAction action = null;

    @Override
    public void sendForm(@NotNull UUID bedrockPlayer) {
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

        List<CustomComponent> components = new ArrayList<>();
        for (CustomComponent component : this.components) {
            components.add(component.withPlaceholders((text) -> PlaceholderUtils.setPlaceholders(player, text)));
        }

        @SuppressWarnings("unchecked")
        org.geysermc.cumulus.CustomForm customForm = org.geysermc.cumulus.CustomForm.of(
                PlaceholderUtils.setPlaceholders(player, super.getTitle()),
                image,
                (List<org.geysermc.cumulus.component.Component>)(List<?>) components // sad noises
        );

        // Set the response handler
        customForm.setResponseHandler((responseData) -> {
            CustomFormResponse response = customForm.parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            Map<String, String> resultPlaceholders = new HashMap<>();
            for (int i = 0; i < components.size(); i++) {

                JsonPrimitive primitive = response.get(i);
                if (primitive == null) {
                    logger.severe("Failed to get response " + i + " from custom form " + super.getTitle());
                    logger.severe("Full response data:");
                    logger.severe(responseData);
                    return;
                }

                resultPlaceholders.put("%result_" + i + "%", primitive.getAsString());
            }

            if (logger.isDebug()) {
                logger.info("Placeholder results for CustomForm " + getTitle());
                for (Map.Entry<String, String> entry : resultPlaceholders.entrySet()) {
                    logger.info(entry.getKey() + ": " + entry.getValue());
                }
            }

            // Handle effects of pressing the button
            InterfaceUtils.affectPlayer(
                    action.withPlaceholders((text) -> PlaceholderUtils.setPlaceholders(player, text, resultPlaceholders)),
                    player);
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(bedrockPlayer, customForm);
    }
}
