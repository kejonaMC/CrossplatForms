package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import lombok.ToString;
import org.geysermc.cumulus.component.LabelComponent;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ToString
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class CustomForm extends BedrockForm {

    private FormImage image = null;
    private List<CustomComponent> components = Collections.emptyList();

    @Required
    private List<Action> actions = null;

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

        List<CustomComponent> components = new ArrayList<>();
        for (CustomComponent component : this.components) {
            components.add(component.withPlaceholders((text) -> placeholders.setPlaceholders(player, text)));
        }

        @SuppressWarnings("unchecked")
        org.geysermc.cumulus.CustomForm form = org.geysermc.cumulus.CustomForm.of(
                placeholders.setPlaceholders(player, super.getTitle()),
                image,
                (List<org.geysermc.cumulus.component.Component>)(List<?>) components // sad noises
        );

        // Set the response handler
        form.setResponseHandler((responseData) -> {
            CustomFormResponse response = form.parseResponse(responseData);
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
            Map<String, String> resultPlaceholders = new HashMap<>();
            for (int i = 0; i < components.size(); i++) {
                CustomComponent component = components.get(i);
                if (component instanceof LabelComponent label) {
                    // label components aren't included in the response
                    resultPlaceholders.put(placeholder(i), label.getText());
                    continue;
                }

                JsonPrimitive result = response.get(i);
                if (result == null) {
                    logger.severe("Failed to get response " + i + " from custom form " + super.getTitle());
                    logger.severe("Full response data:");
                    logger.severe(responseData);
                    return;
                }

                resultPlaceholders.put(placeholder(i), component.parse(result));
            }

            if (logger.isDebug()) {
                logger.info("Placeholder results for CustomForm " + getTitle());
                for (Map.Entry<String, String> entry : resultPlaceholders.entrySet()) {
                    logger.info(entry.getKey() + ": " + entry.getValue());
                }
            }

            // Handle effects of pressing the button
            for (Action action : actions) {
                action.affectPlayer(player, resultPlaceholders, interfaceManager, bedrockHandler);
            }
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }

    private String placeholder(int i) {
        return "%result_" + i + "%";
    }
}
