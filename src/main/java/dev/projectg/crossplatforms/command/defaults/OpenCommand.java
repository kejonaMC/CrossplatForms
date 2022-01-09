package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.form.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.handler.server.ServerHandler;
import dev.projectg.crossplatforms.utils.InterfaceUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

@RequiredArgsConstructor
public class OpenCommand implements FormsCommand {

    private static final String NAME = "open";
    private static final String PERMISSION = "crossplatforms.open";
    private static final String PERMISSION_OTHER = "crossplatforms.open.other";

    private final ServerHandler serverHandler;
    private final BedrockFormRegistry bedrockRegistry;
    private final JavaMenuRegistry javaRegistry;

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.of("form"))
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .handler(context -> {
                    Player player = Bukkit.getPlayer(context.getSender().getUUID().get());
                    Objects.requireNonNull(player);
                    InterfaceUtils.sendInterface(player, bedrockRegistry, javaRegistry, context.get("form"));
                })
                .build()
        );

        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.of("form"))
                .argument(StringArgument.of("player", StringArgument.StringMode.SINGLE))
                .permission(PERMISSION_OTHER)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    Player player = Bukkit.getPlayer(target);
                    if (player == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "That player doesn't exist!");
                    } else {
                        InterfaceUtils.sendInterface(player, bedrockRegistry, javaRegistry, context.get("form"));
                    }
                })
                .build()
        );
    }
}
