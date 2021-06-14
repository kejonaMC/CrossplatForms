package dev.projectg.geyserhub.module.menu.bedrock.button;

import org.geysermc.cumulus.util.FormImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CommandButton extends Button {

    private final List<String> commands;

    public CommandButton(@Nonnull List<String> commands, @Nonnull String text) {
        super(text);
        this.commands = commands;
    }
    public CommandButton(@Nonnull List<String> commands, @Nonnull String text, @Nullable FormImage image) {
        super(text, image);
        this.commands = commands;
    }
    public CommandButton(@Nonnull List<String> commands, @Nonnull String text, @Nonnull FormImage.Type type, @Nonnull String data) {
        super(text, type, data);
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }
}
