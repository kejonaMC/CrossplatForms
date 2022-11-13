package dev.kejona.crossplatforms.interfacing.bedrock.simple;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.filler.SimpleFormFiller;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import lombok.ToString;
import org.geysermc.cumulus.form.SimpleForm;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ToString(callSuper = true)
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleBedrockForm extends BedrockForm {

    public static final String TYPE = "simple_form";

    private String content = "";
    private List<SimpleButton> buttons = Collections.emptyList();
    private List<SimpleFormFiller> fillers = Collections.emptyList();

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void send(@Nonnull FormPlayer player, @Nonnull Resolver resolver) {
        Logger logger = Logger.get();
        UUID uuid = player.getUuid();
        if (!bedrockHandler.isBedrockPlayer(uuid)) {
            logger.severe(player.getName() + " with UUID " + uuid + " is not a Bedrock Player!");
            return;
        }

        SimpleForm.Builder form = SimpleForm.builder()
            .title(resolver.apply(getTitle()))
            .content(resolver.apply(content));

        // make a copy of the buttons
        List<SimpleButton> buttons = new ArrayList<>(this.buttons);

        // fill the copy with additional buttons
        for (SimpleFormFiller filler : fillers) {
            int index = filler.insertIndex();
            if (index < 0) {
                filler.generateButtons(resolver).forEachOrdered(buttons::add);
            } else {
                filler.generateButtons(resolver).forEachOrdered(b -> buttons.add(index, b));
            }
        }

        // resolve relevant placeholders and add it to the form
        buttons.forEach(button -> button.addTo(form, resolver));

        // actions for incorrect response (closed or invalid response)
        form.closedOrInvalidResultHandler((result) -> handleIncorrect(player, resolver, result));

        // actions for correct response
        form.validResultHandler(response -> executeHandler(
            () -> buttons.get(response.clickedButtonId()).click(player, this, resolver)
        ));

        // Send the form to the floodgate player
        bedrockHandler.sendForm(uuid, form.build());
    }
}
