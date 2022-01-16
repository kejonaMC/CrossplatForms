package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class OpenCommand extends FormsCommand {

    private static final String NAME = "open";
    private static final String PERMISSION = "crossplatforms.command." + NAME;
    private static final String PERMISSION_OTHER = PERMISSION + ".others";

    private static final String ARGUMENT = "form|menu";

    public OpenCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        BedrockFormRegistry bedrockRegistry = crossplatForms.getBedrockFormRegistry();
        JavaMenuRegistry javaRegistry = crossplatForms.getJavaMenuRegistry();

        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.of(ARGUMENT))
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .handler(context -> {
                    Player player = Bukkit.getPlayer(context.getSender().getUUID().orElseThrow());
                    Objects.requireNonNull(player);
                    InterfaceUtils.sendInterface(player, bedrockRegistry, javaRegistry, context.get(ARGUMENT));
                })
                .build()
        );

        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.of(ARGUMENT))
                .argument(StringArgument.of("player", StringArgument.StringMode.SINGLE))
                .permission(PERMISSION_OTHER)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    Player player = Bukkit.getPlayer(target);
                    if (player == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "That player doesn't exist!");
                    } else {
                        InterfaceUtils.sendInterface(player, bedrockRegistry, javaRegistry, context.get(ARGUMENT));
                    }
                })
                .build()
        );
    }
}
