package dev.projectg.geyserhub.module.menu;

import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class Button {

    // base requirement for ButtonComponent
    private String text;
    private FormImage image;

    // Everything extra
    private List<String> commands;
    private String server;

    /**
     * Create a button.
     * @param text the text of the button
     */
    public Button(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
    }

    /**
     * Set the text of the button.
     * @param text the new text
     * @return the same Button instance
     */
    public Button setText(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
        return this;
    }

    /**
     * Set the image of the button.
     * @param image the image
     * @return the same Button instance
     */
    public Button setImage(@Nullable FormImage image) {
        this.image = image;
        return this;
    }

    /**
     * Set the image of the button.
     * @param type the type of image
     * @param data the image data
     * @return the same Button instance
     */
    public Button setImage(@Nonnull FormImage.Type type, @Nonnull String data) {
        this.image = FormImage.of(Objects.requireNonNull(type), Objects.requireNonNull(data));
        return this;
    }

    /**
     * set the commands list.
     * @param commands the commands list
     * @return the same Button instance
     */
    public Button setCommands(@Nullable List<String> commands) {
        this.commands = commands;
        return this;
    }

    /**
     * Set the server name.
     * @param server the server name
     * @return the same Button instance
     */
    public Button setServer(@Nullable String server) {
        this.server = server;
        return this;
    }

    /**
     * Get the button component based off the text and image off the Button.
     * @return the button component
     */
    public ButtonComponent getButtonComponent() {
        return ButtonComponent.of(text, image);
    }

    @Nonnull
    public String getText() {
        return this.text;
    }

    @Nullable
    public FormImage getImage() {
        return this.image;
    }

    @Nullable
    public List<String> getCommands() {
        return this.commands;
    }

    @Nullable
    public String getServer() {
        return this.server;
    }
}
