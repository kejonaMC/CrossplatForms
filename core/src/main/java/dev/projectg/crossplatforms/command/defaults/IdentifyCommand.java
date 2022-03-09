package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class IdentifyCommand extends FormsCommand {

    public static final String NAME = "identify";
    public static final String PERMISSION = PERMISSION_BASE + NAME;
    public static final String PERMISSION_OTHER = PERMISSION + ".others";

    public IdentifyCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        BedrockHandler bedrockHandler = crossplatForms.getBedrockHandler();
        ServerHandler serverHandler = crossplatForms.getServerHandler();

        manager.command(defaultBuilder
                .literal(NAME)
                .permission(sender -> sender.hasPermission(PERMISSION) && sender.isPlayer())
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String message;
                    if (bedrockHandler.isBedrockPlayer(origin.getUUID().orElseThrow(NoSuchElementException::new))) {
                        message = "You are a bedrock player";
                    } else {
                        message = "You are not a bedrock player";
                    }
                    origin.sendMessage(Logger.Level.INFO, message);
                }).build());

        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.<CommandOrigin>newBuilder("player")
                        .withSuggestionsProvider((context, s) -> serverHandler.getPlayers().stream().map(FormPlayer::getName).collect(Collectors.toList()))
                        .build())
                .permission(PERMISSION_OTHER)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String targetName = context.get("player");
                    FormPlayer target = serverHandler.getPlayer(targetName);
                    if (target == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "That player doesn't exist");
                    } else {
                        String message = targetName + (bedrockHandler.isBedrockPlayer(target.getUuid()) ? " is a Bedrock player" : " is not a Bedrock player");
                        origin.sendMessage(Logger.Level.INFO, message);
                    }
                })
                .build()
        );
    }
}
