package dev.projectg.crossplatforms.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import lombok.RequiredArgsConstructor;

/**
 * Extending classes are expected to register one or more commands with the {@link CommandManager} given in {@link FormsCommand#register(CommandManager, Command.Builder)}
 * Additionally, it is expected that any necessary permissions will be registered using {@link CrossplatForms#getServerHandler()}
 */
@RequiredArgsConstructor
public abstract class FormsCommand {

    public static final String NAME = "forms";

    public static final String PERMISSION_BASE = Constants.ID + ".command.";

    protected final CrossplatForms crossplatForms;

    /**
     * Register a command
     * @param manager The command manager to register to
     * @param defaultBuilder The command builder to be used if registering a subcommand to the default base command
     */
    public abstract void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder);
}
