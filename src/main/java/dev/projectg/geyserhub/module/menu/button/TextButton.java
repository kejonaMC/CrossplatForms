package dev.projectg.geyserhub.module.menu.button;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TextButton {

    @Nonnull private String text;

    /**
     * Create a button.
     * @param text the text of the button
     */
    public TextButton(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
    }

    /**
     * Copy constructor.
     * @param button The button to make a copy of
     */
    public TextButton(@Nonnull TextButton button) {
        this.text = button.text;
    }

    /**
     * Set the text of the button.
     * @param text the new text
     */
    public void setText(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
    }

    public @Nonnull String getText() {
        return this.text; // Strings are immutable
    }

}
