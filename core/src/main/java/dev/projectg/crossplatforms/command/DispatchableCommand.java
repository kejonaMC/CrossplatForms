package dev.projectg.crossplatforms.command;

import org.spongepowered.configurate.objectmapping.meta.Required;

/**
 * Used for running commands as a player or as the console
 * @param player The player to run the command as. null for console.
 * @param command The command string to run
 * @param op If given a player, whether or not the player should be temporarily op'd when running the command.
 */
public record DispatchableCommand(@Required boolean player, @Required String command, @Required boolean op) {

    /**
     * Create a dispatchable command for the server console
     * @param command The command to run
     */
    public DispatchableCommand(String command) {
        this(false, command, true);
    }

    public DispatchableCommand withCommand(String command) {
        if (this.command.equals(command)) {
            return this;
        } else {
            return new DispatchableCommand(this.player, command, this.op);
        }
    }
}
