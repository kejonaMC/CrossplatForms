package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.kejona.crossplatforms.Constants;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;

public class VersionCommand extends FormsCommand {

    public static final String NAME = "version";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

    public VersionCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        manager.command(defaultBuilder.literal(NAME)
                .permission(PERMISSION)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    origin.sendMessage("CrossplatForms version:");
                    origin.sendMessage("Version: " + Constants.version() + ", Branch: " + Constants.branch() + ", Build: " + Constants.buildNumber() + ", Commit: " + Constants.commit());
                })
                .build());
    }
}
