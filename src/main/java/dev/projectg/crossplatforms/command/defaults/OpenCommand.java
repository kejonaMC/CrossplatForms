package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.Player;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class OpenCommand extends FormsCommand {

    private static final String NAME = "open";
    private static final String PERMISSION = PERMISSION_BASE + NAME;
    private static final String PERMISSION_OTHER = PERMISSION + ".others";

    private static final String ARGUMENT = "form|menu";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;
    private final BedrockFormRegistry bedrockRegistry;
    private final JavaMenuRegistry javaRegistry;

    public OpenCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);

        this.serverHandler = crossplatForms.getServerHandler();
        this.bedrockHandler = crossplatForms.getBedrockHandler();
        this.bedrockRegistry = crossplatForms.getInterfaceManager().getBedrockRegistry();
        this.javaRegistry = crossplatForms.getInterfaceManager().getJavaRegistry();
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        InterfaceManager interfaceManager = crossplatForms.getInterfaceManager();

        // Suggestion provider
        BiFunction<CommandContext<CommandOrigin>, String, List<String>> suggestions = (context, string) -> {
            CommandOrigin origin = context.getSender();

            List<Interface> interfaces = new ArrayList<>();

            // If the player has permission to make other players open forms, don't filter the suggestions
            if (origin.isPlayer() && !origin.hasPermission(PERMISSION_OTHER)) {
                Player player = serverHandler.getPlayer(origin.getUUID().orElseThrow());
                if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
                    interfaces.addAll(bedrockRegistry.getForms().values());
                } else {
                    interfaces.addAll(javaRegistry.getMenus().values());
                }
            } else {
                if (bedrockHandler.getPlayerCount() > 0) {
                    interfaces.addAll(bedrockRegistry.getForms().values());
                }
                interfaces.addAll(javaRegistry.getMenus().values());
            }

            return interfaces.stream()
                    .filter(ui -> origin.hasPermission(ui.permission(Interface.Limit.COMMAND)))
                    .map(Interface::getIdentifier)
                    .distinct()
                    .collect(Collectors.toList());
        };

        CommandArgument<CommandOrigin, String> formArgument = StringArgument.<CommandOrigin>newBuilder(ARGUMENT).asRequired().withSuggestionsProvider(suggestions).build();

        Command.Builder<CommandOrigin> firstArg = defaultBuilder
                .literal(NAME)
                .argument(formArgument);

        // Base open command
        manager.command(firstArg
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .handler(context -> {
                    Player player = serverHandler.getPlayer(context.getSender().getUUID().orElseThrow());
                    interfaceManager.sendInterface(player, context.get(ARGUMENT));
                }));

        // Additional command to make other players open a form or menu
        manager.command(firstArg
                .argument(StringArgument.<CommandOrigin>newBuilder("player")
                        .single()
                        .asRequired()
                        .withSuggestionsProvider((context, string) -> {
                            String form = context.get(ARGUMENT);
                            boolean isBedrock = bedrockRegistry.getForms().containsKey(form);
                            boolean isJava = javaRegistry.getMenus().containsKey(form);
                            return new ArrayList<>(serverHandler.getPlayers())
                                    .stream()
                                    .filter(player -> {
                                        // Only show players that can receive the inputted form/menu
                                        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
                                            return isBedrock;
                                        } else {
                                            return isJava;
                                        }
                                    })
                                    .map(Player::getName)
                                    .collect(Collectors.toList());
                        })
                        .build())
                .permission(PERMISSION_OTHER)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    Player player = serverHandler.getPlayer(target);
                    if (player == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "The player " + target + " doesn't exist!");
                    } else {
                        interfaceManager.sendInterface(player, context.get(ARGUMENT));
                    }
                })
                .build()
        );
    }
}
