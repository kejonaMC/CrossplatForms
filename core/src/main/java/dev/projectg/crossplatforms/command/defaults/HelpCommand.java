package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;

public class HelpCommand extends FormsCommand {

    public static final String NAME = "help";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

    private final MinecraftHelp<CommandOrigin> minecraftHelp;

    public HelpCommand(CrossplatForms crossplatForms, MinecraftHelp<CommandOrigin> minecraftHelp) {
        super(crossplatForms);
        this.minecraftHelp = minecraftHelp;
    }

    @SuppressWarnings("ConstantConditions") // CommandContext#getOrDefault will not return null if given nonnull default
    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        manager.command(defaultBuilder
            .literal(NAME)
            .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
            .handler(context -> minecraftHelp.queryCommands(context.getOrDefault("query", ""), context.getSender()))
        );
    }
}
