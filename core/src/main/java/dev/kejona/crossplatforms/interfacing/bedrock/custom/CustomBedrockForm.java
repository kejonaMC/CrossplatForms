package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.IllegalValueException;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.resolver.MapResolver;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.ValuedType;
import lombok.ToString;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.util.AbsentComponent;
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

@ToString(callSuper = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class CustomBedrockForm extends BedrockForm implements ValuedType {

    public static final String TYPE = "custom_form";

    @Nullable
    private String image = null;
    private List<CustomComponent> components = Collections.emptyList();
    private List<Action<? super CustomBedrockForm>> actions = Collections.emptyList();

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void send(@Nonnull FormPlayer player, @Nonnull Resolver resolver) {
        Logger logger = Logger.get();
        UUID uuid = player.getUuid();

        if (!bedrockHandler.isBedrockPlayer(uuid)) {
            logger.severe("Player with UUID " + uuid + " is not a Bedrock Player!");
            return;
        }

        CustomForm.Builder builder = CustomForm.builder().title(resolver.apply(super.getTitle()));

        FormImage image = createFormImage(resolver.apply(this.image));
        if (image != null) {
            // cleanup when cumulus gets CustomForm.Builder#icon(@Nullable FormImage) method
            builder.icon(image.type(), image.data());
        }

        // Setup and add components
        List<CustomComponent> formatted = new ArrayList<>();
        try {
            for (CustomComponent component : this.components) {
                // resolve placeholders
                CustomComponent prepared = component.preparedCopy(resolver);
                formatted.add(prepared);

                // add component to form
                builder.optionalComponent(prepared.cumulusComponent(), prepared.show());
            }
        } catch (IllegalValueException e) {
            player.warn("There was an error sending a form to you.");
            logger.severe("Failed to send form " + identifier + " to " + player.getName() + " because the " + e.identifier() + " of component " + components.size() + " was '" + e.value() + "' and could not be converted to a " + e.expectedType());
            return;
        }

        builder.closedOrInvalidResultHandler(() -> handleIncorrect(player, resolver));

        builder.validResultHandler((form, response) -> executeHandler(() -> {
            response.includeLabels(true); // allow label to be used as result placeholder

            Map<String, String> resultPlaceholders = new HashMap<>();
            for (int i = 0; i < formatted.size(); i++) {
                CustomComponent component = formatted.get(i);
                Object result = response.valueAt(i);

                String value;
                if (result == null || result instanceof AbsentComponent) {
                    // If the result is null then the Component should be a Label
                    // If it is an AbsentComponent then the Component was optional and was not shown
                    // todo: optional components that were not shown will return null when https://github.com/GeyserMC/Cumulus/pull/6 is merged
                    value = component.resultIfHidden();
                } else {
                    value = result.toString();
                }
                resultPlaceholders.put(placeholder(i), component.parse(player, value));
            }

            if (logger.isDebug()) {
                logger.info("Placeholder results for CustomForm " + getTitle());
                for (Map.Entry<String, String> entry : resultPlaceholders.entrySet()) {
                    logger.info(entry.getKey() + ": " + entry.getValue());
                }
            }

            Resolver subResolver = new MapResolver(resultPlaceholders).andThen(resolver);

            // Handle effects of pressing the button
            Action.affectPlayer(player, actions, subResolver, this);
        }));

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, builder.build());
    }

    private static String placeholder(int i) {
        return "%result_" + i + "%";
    }
}
