package dev.projectg.crossplatforms.interfacing.bedrock.simple;


import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.action.Action;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleButton {

    @Required
    private String text;

    @Nullable
    private FormImage image;

    private List<Action> actions = Collections.emptyList();

    /**
     * Create an immutable copy of the current SimpleButton, with the new text applied.
     * @param resolver The placeholder resolver to use
     * @return A new instance with the given text
     */
    @Contract(pure = true)
    public SimpleButton withPlaceholders(Resolver resolver) {
        // Lombok's with doesn't super superclass fields.
        SimpleButton button = new SimpleButton();
        button.text = resolver.apply(this.text);
        button.image = this.image; // form image is immutable
        button.actions = new ArrayList<>(this.actions);
        return button;
    }

    public ButtonComponent cumulusComponent() {
        return ButtonComponent.of(text, image);
    }
}
