package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;

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
                    origin.sendMessage(message);
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
                        origin.warn("That player doesn't exist");
                    } else {
                        String message = targetName + (bedrockHandler.isBedrockPlayer(target.getUuid()) ? " is a Bedrock player" : " is not a Bedrock player");
                        origin.sendMessage(message);
                    }
                })
                .build()
        );
    }
}
