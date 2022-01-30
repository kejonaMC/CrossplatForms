package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;

public class HelpCommand extends FormsCommand {

    public static final String NAME = "help";
    public static final String PERMISSION = "crossplatforms.command." + NAME;

    private final MinecraftHelp<CommandOrigin> minecraftHelp;

    public HelpCommand(CrossplatForms crossplatForms, MinecraftHelp<CommandOrigin> minecraftHelp) {
        super(crossplatForms);
        this.minecraftHelp = minecraftHelp;
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        manager.command(defaultBuilder.literal(NAME)
                .handler(context -> minecraftHelp.queryCommands("", context.getSender()))
        );
    }
}
