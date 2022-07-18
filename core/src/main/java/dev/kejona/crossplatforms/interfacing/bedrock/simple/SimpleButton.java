package dev.kejona.crossplatforms.interfacing.bedrock.simple;


import dev.kejona.crossplatforms.Resolver;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.filler.FillerUtils;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.FormImageSerializer;
import dev.kejona.crossplatforms.interfacing.bedrock.OptionalElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.TypeSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleButton extends OptionalElement {

    private static final TypeSerializer<FormImage> IMAGE_SERIALIZER = new FormImageSerializer();

    @Nullable
    private String text;

    @Nullable
    private String image = null;

    @Nonnull
    private List<Action> actions = Collections.emptyList();

    public SimpleButton(@Nonnull String text) {
        this.text = text;
    }

    public SimpleButton(@Nonnull String text, @Nullable String image) {
        this.text = text;
        this.image = image;
    }

    @Nonnull
    public String getText() {
        if (text == null) {
            return "";
        }
        return text;
    }

    /**
     * Create an immutable copy of the current SimpleButton, with the new text applied.
     *
     * @param resolver The placeholder resolver to use
     * @return A new instance with the given text
     */
    @Contract(pure = true)
    public SimpleButton withPlaceholders(Resolver resolver) {
        SimpleButton button = new SimpleButton();
        if (this.text != null) {
            button.text = resolver.apply(this.text);
        }
        if (this.image != null) {
            button.image = resolver.apply(this.image);
        }
        button.actions = new ArrayList<>(this.actions);
        button.shouldShow = this.shouldShow.stream().map(resolver).collect(Collectors.toList());
        return button;
    }

    public ButtonComponent cumulusComponent() {
        if (image == null) {
            return ButtonComponent.of(getText());
        } else {
            return ButtonComponent.of(getText(), BedrockForm.getFormImage(image));
        }
    }

    public SimpleButton format(SimpleButton format) {
        SimpleButton button = new SimpleButton();
        button.text = FillerUtils.replace(format.text, this.text);
        button.image = FillerUtils.replace(format.image, this.image);

        // we don't support generating actions
        button.actions = format.actions;
        return button;
    }
}
