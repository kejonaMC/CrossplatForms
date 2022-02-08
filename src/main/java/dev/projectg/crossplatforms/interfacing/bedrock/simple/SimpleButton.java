package dev.projectg.crossplatforms.interfacing.bedrock.simple;


import dev.projectg.crossplatforms.interfacing.BasicClickAction;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Contract;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class SimpleButton extends BasicClickAction implements ButtonComponent {

    @Required
    private String text;

    @Nullable
    private FormImage image;

    /**
     * Create an immutable copy of the current SimpleButton, with the new text applied.
     * @param text The new text to apply
     * @return A new instance with the given text
     */
    @Contract(pure = true)
    public SimpleButton withText(@Nonnull String text) {
        // Lombok's with doesn't super superclass fields.
        SimpleButton button = new SimpleButton();
        button.text = text;
        button.image = this.image; // form image is immutable
        button.commands = new ArrayList<>(this.commands);
        button.server = this.server;
        return button;
    }
}
