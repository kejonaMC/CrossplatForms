package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;

public class ReloadCommand extends FormsCommand {

    private static final String NAME = "reload";
    private static final String PERMISSION = "crossplatforms.command" + NAME;

    public ReloadCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        manager.command(defaultBuilder
                .literal(NAME)
                .permission(PERMISSION)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    if (!ReloadableRegistry.reloadAll() && origin.isPlayer()) {
                        origin.sendMessage(Logger.Level.SEVERE, "There was an error reloading something! Please check the server console for further information.");
                    }
                })
                .build());
    }
}
