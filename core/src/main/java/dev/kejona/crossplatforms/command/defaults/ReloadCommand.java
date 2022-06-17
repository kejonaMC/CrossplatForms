package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import dev.kejona.crossplatforms.reloadable.ReloadableRegistry;

public class ReloadCommand extends FormsCommand {

    public static final String NAME = "reload";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

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
                    boolean success = ReloadableRegistry.reloadAll();
                    if (!origin.isConsole()) {
                        // reloadable registry handles console messages
                        if (success) {
                            origin.sendMessage("Successfully reloaded");
                        } else {
                            origin.severe("There was an error reloading something! Please check the server console for further information.");
                        }
                    }
                })
                .build());
    }
}
