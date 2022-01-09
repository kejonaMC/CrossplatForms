package dev.projectg.crossplatforms.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;

public interface FormsCommand {

    /**
     * Register a command
     * @param manager The command manager to register to
     * @param defaultBuilder The command builder to be used if registering a subcommand to the default base command
     */
    void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder);
}
