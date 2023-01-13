package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;

public class HelpCommand extends FormsCommand {

    public static final String NAME = "help";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

    private final MinecraftHelp<CommandOrigin> minecraftHelp;

    public HelpCommand(CrossplatForms crossplatForms, MinecraftHelp<CommandOrigin> minecraftHelp) {
        super(crossplatForms);
        this.minecraftHelp = minecraftHelp;
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        manager.command(defaultBuilder
            .literal(NAME)
            .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
            .handler(context -> minecraftHelp.queryCommands(context.getOrDefault("query", ""), context.getSender()))
        );
    }
}
