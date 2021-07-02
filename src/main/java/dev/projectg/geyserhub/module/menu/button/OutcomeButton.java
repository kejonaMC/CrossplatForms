package dev.projectg.geyserhub.module.menu.button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OutcomeButton extends TextButton {

    @Nonnull private List<String> commands = Collections.emptyList();
    @Nullable private String server;

    /**
     * Create a button.
     * @param text the text of the button
     */
    public OutcomeButton(@Nonnull String text) {
        super(text);
    }

    /**
     * Copy constructor.
     * @param button The button to make a copy of
     */
    public OutcomeButton(@Nonnull OutcomeButton button) {
        super(button);
        this.commands = button.getCommands(); // lists are mutable, everything else here isn't
        this.server = button.getServer();
    }

    /**
     * Set the commands list.
     * @param commands the commands list, which can be empty
     */
    public void setCommands(@Nonnull List<String> commands) {
        Objects.requireNonNull(commands);
        this.commands = new ArrayList<>(commands);
    }

    /**
     * Set the server name.
     * @param server the server name
     */
    public void setServer(@Nullable String server) {
        this.server = server;
    }

    /**
     * Get the commands that should be executed when this button is pressed.
     * @return a List of commands, which may be empty
     */
    public @Nonnull List<String> getCommands() {
        return new ArrayList<>(this.commands); // Lists are mutable
    }

    public @Nullable String getServer() {
        return this.server; // Strings are immutable
    }
}
