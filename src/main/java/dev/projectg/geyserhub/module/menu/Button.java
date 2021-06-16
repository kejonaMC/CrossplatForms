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

    public Button(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
    }


    public Button setText(@Nonnull String text) {
        this.text = Objects.requireNonNull(text);
        return this;
    }

    public Button setImage(@Nullable FormImage image) {
        this.image = image;
        return this;
    }
    public Button setImage(@Nonnull FormImage.Type type, @Nonnull String data) {
        this.image = FormImage.of(Objects.requireNonNull(type), Objects.requireNonNull(data));
        return this;
    }
    public Button setCommands(@Nullable List<String> commands) {
        this.commands = commands;
        return this;
    }
    public Button setServer(@Nullable String server) {
        this.server = server;
        return this;
    }

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
