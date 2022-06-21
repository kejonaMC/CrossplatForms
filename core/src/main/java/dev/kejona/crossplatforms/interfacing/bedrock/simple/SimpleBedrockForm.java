package dev.kejona.crossplatforms.interfacing.bedrock.simple;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import lombok.ToString;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleBedrockForm extends BedrockForm {

    public static final String TYPE = "simple_form";

    private String content = "";
    private List<SimpleButton> buttons = Collections.emptyList();

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

        SimpleForm.Builder form = SimpleForm.builder()
            .title(placeholders.setPlaceholders(player, super.getTitle()))
            .content(placeholders.setPlaceholders(player, content));

        // setup and add buttons
        List<SimpleButton> formattedButtons = new ArrayList<>(); // as our custom buttons
        for (SimpleButton button : buttons) {
            SimpleButton resolved = button.withPlaceholders(placeholders.resolver(player));
            formattedButtons.add(resolved);

            form.button(resolved.getText(), resolved.getImage());
        }

        // actions for incorrect response (closed or invalid response)
        form.closedOrInvalidResultHandler(() -> handleIncorrect(player));

        // actions for correct response
        form.validResultHandler(response -> executeHandler(() -> {
            // Handle effects of pressing the button
            List<Action> actions = formattedButtons.get(response.clickedButtonId()).getActions();
            Action.affectPlayer(player, actions);
        }));

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form.build());
    }
}
