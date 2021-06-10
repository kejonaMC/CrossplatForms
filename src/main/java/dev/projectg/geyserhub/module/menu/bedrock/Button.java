package dev.projectg.geyserhub.module.menu.bedrock;

import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Button {

    private final ButtonComponent buttonComponent;

    // This class should only be constructed if its being inherited, otherwise it is pointless (just construct a ButtonComponent)
    protected Button(@Nonnull String text) {
        this.buttonComponent = ButtonComponent.of(text);
    }
    protected Button(@Nonnull String text, @Nullable FormImage image) {
        this.buttonComponent = ButtonComponent.of(text, image);
    }
    protected Button(@Nonnull String text, @Nonnull FormImage.Type type, @Nonnull String data) {
        this.buttonComponent = ButtonComponent.of(text, type, data);
    }

    public ButtonComponent getButtonComponent() {
        return buttonComponent;
    }
}
