package dev.kejona.crossplatforms.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class DispatchableCommand {

    private final boolean player;
    private final String command;
    private final boolean op;

    /**
     * Used for running commands as a player or as the console
     *
     * @param player  The player to run the command as. null for console.
     * @param command The command string to run
     * @param op      If given a player, whether or not the player should be temporarily op'd when running the command.
     */
    public DispatchableCommand(boolean player, String command, boolean op) {
        if (!player && op) {
            throw new IllegalArgumentException("player is false but op is true");
        }

        this.player = player;
        this.command = command;
        this.op = op;
    }

    /**
     * Create a dispatchable command for the server console
     * @param command The command to run
     */
    public DispatchableCommand(String command) {
        this(false, command, false);
    }

    public DispatchableCommand withCommand(String command) {
        if (this.command.equals(command)) {
            return this;
        } else {
            return new DispatchableCommand(this.player, command, this.op);
        }
    }
}
