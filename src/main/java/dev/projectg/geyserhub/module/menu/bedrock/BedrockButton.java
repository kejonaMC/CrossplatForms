package dev.projectg.geyserhub.module.menu.bedrock;

import dev.projectg.geyserhub.module.menu.button.OutcomeButton;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BedrockButton extends OutcomeButton {

    @Nullable private FormImage image;

    /**
     * Create a button.
     * @param text the text of the button
     */
    public BedrockButton(@Nonnull String text) {
        super(text);
    }

    /**
     * Copy constructor.
     * @param button The button to make a copy of
     */
    public BedrockButton(@Nonnull BedrockButton button) {
        super(button);
        this.image = button.image;
    }

    /**
     * Set the image of the button.
     * @param image the image
     */
    public void setImage(@Nullable FormImage image) {
        this.image = image;
    }

    public @Nullable FormImage getImage() {
        return this.image; // Form image is immutable
    }

    /**
     * Get the button component based off the text and image off the Button.
     * @return the button component
     */
    public @NonNull ButtonComponent getButtonComponent() {
        return ButtonComponent.of(getText(), getImage());
    }

}
