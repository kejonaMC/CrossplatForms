package dev.kejona.crossplatforms.interfacing.bedrock.simple;

import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.filler.SimpleFormFiller;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.OptionalElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleButton extends OptionalElement {

    @Nullable
    private String text;

    @Setting(value = "image")
    @Nullable
    private String imageData;

    @Nonnull
    private List<Action> actions = Collections.emptyList();

    @Nullable
    private transient SimpleButton raw = null;

    public SimpleButton(@Nonnull String text) {
        this.text = text;
    }

    public SimpleButton(@Nonnull String text, @Nullable String imageData) {
        this.text = text;
        this.imageData = imageData;
    }

    @Nonnull
    public String getText() {
        if (text == null) {
            return "";
        }
        return text;
    }

    @Nonnull
    private String getImage() {
        if (imageData == null) {
            return "";
        }
        return imageData;
    }

    private Map<String, String> additionalPlaceholders() {
        if (raw == null) {
            return Collections.emptyMap();
        } else {
            Map<String, String> additionalPlaceholders = new HashMap<>(2);
            additionalPlaceholders.put("%raw_text%", getText());
            additionalPlaceholders.put("%raw_image%", getImage());
            return additionalPlaceholders;
        }
    }

    /**
     * Formats a copy of this SimpleButton with the given raw SimpleButton. This is only intended for use with
     * {@link SimpleFormFiller}, where the raw parameter is generated
     */
    @Contract("_ -> new")
    public SimpleButton withRaw(SimpleButton raw) {
        SimpleButton copy = new SimpleButton();
        if (this.text == null) {
            copy.text = raw.text; // user did not override, use raw as text
        } else {
            copy.text = this.text;
        }

        if (this.imageData == null) {
            copy.imageData = raw.imageData; // user did not override...
        } else {
            copy.imageData = this.imageData;
        }

        copy.actions = this.actions; // we don't support generated actions
        copy.raw = raw; // used for placeholders later on
        copy.shouldShow = this.shouldShow; // we don't support generated requirements
        return copy;
    }

    public void addTo(SimpleForm.Builder form, Resolver resolver) {
        Map<String, String> morePlaceholders = additionalPlaceholders();

        String display;
        if (text == null || text.isEmpty()) {
            display = "";
        } else {
            display = resolver.apply(text, morePlaceholders);
        }

        FormImage image;
        if (imageData == null || imageData.isEmpty()) {
            image = null;
        } else {
            image = BedrockForm.createFormImage(resolver.apply(imageData, morePlaceholders));
        }

        boolean show = allTrue(shouldShow.stream().map(s -> resolver.apply(s, morePlaceholders)));

        form.optionalButton(display, image, show);
    }

    public void click(FormPlayer player) {
        if (raw == null) {
            Action.affectPlayer(player, actions);
        } else {
            Action.affectPlayer(player, actions, additionalPlaceholders());
        }
    }
}
