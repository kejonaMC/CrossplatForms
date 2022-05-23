package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.gson.JsonPrimitive;
import dev.projectg.crossplatforms.IllegalValueException;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.serialize.ValuedType;
import lombok.ToString;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@ToString(callSuper = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class CustomForm extends BedrockForm implements ValuedType {

    public static final String TYPE = "custom_form";

    @Nullable
    private FormImage image = null;
    private List<CustomComponent> components = Collections.emptyList();
    private List<Action> actions = Collections.emptyList();

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

        List<CustomComponent> customComponents = new ArrayList<>(); // as our custom components
        List<Component> components = new ArrayList<>(); // as "vanilla" cumulus
        try {
            for (CustomComponent component : this.components) {
                CustomComponent resolved = component.withPlaceholders(placeholders.resolver(player));
                customComponents.add(resolved);
                components.add(resolved.cumulusComponent());
            }
        } catch (IllegalValueException e) {
            player.warn("There was an error sending a form to you.");
            logger.severe("Failed to send form " + identifier + " to " + player.getName() + " because the " + e.identifier() + " of component " + components.size() + " was '" + e.value() + "' and could not be converted to a " + e.expectedType());
            return;
        }

        org.geysermc.cumulus.CustomForm form = org.geysermc.cumulus.CustomForm.of(
                placeholders.setPlaceholders(player, super.getTitle()),
                image,
                components
        );

        // Set the response handler
        Consumer<String> handler = (responseData) -> {
            CustomFormResponse response = form.parseResponse(responseData);
            if (!response.isCorrect()) {
                handleIncorrect(player);
                return;
            }
            Map<String, String> resultPlaceholders = new HashMap<>();
            for (int i = 0; i < customComponents.size(); i++) {
                CustomComponent component = customComponents.get(i);
                if (component instanceof Label) {
                    Label label = (Label) component;
                    // label components aren't included in the response
                    resultPlaceholders.put(placeholder(i), label.text());
                    continue;
                }

                JsonPrimitive result = response.get(i);
                if (result == null) {
                    logger.severe("Failed to get response " + i + " from custom form " + super.getTitle());
                    logger.severe("Full response data:");
                    logger.severe(responseData);
                    return;
                }

                resultPlaceholders.put(placeholder(i), component.parse(player, result.getAsString()));
            }

            if (logger.isDebug()) {
                logger.info("Placeholder results for CustomForm " + getTitle());
                for (Map.Entry<String, String> entry : resultPlaceholders.entrySet()) {
                    logger.info(entry.getKey() + ": " + entry.getValue());
                }
            }

            // Handle effects of pressing the button
            Action.affectPlayer(player, actions, resultPlaceholders);
        };

        setResponseHandler(form, handler);

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form);
    }

    private String placeholder(int i) {
        return "%result_" + i + "%";
    }
}
