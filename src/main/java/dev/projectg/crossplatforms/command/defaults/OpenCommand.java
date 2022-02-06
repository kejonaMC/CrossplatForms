package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OpenCommand extends FormsCommand {

    public static final String NAME = "open";
    public static final String PERMISSION = PERMISSION_BASE + NAME;
    public static final String PERMISSION_OTHER = PERMISSION + ".others";
    private static final String ARGUMENT = "form|menu";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;
    private final InterfaceManager interfaceManager;
    private final BedrockFormRegistry bedrockRegistry;
    private final JavaMenuRegistry javaRegistry;

    public OpenCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);

        this.serverHandler = crossplatForms.getServerHandler();
        this.bedrockHandler = crossplatForms.getBedrockHandler();
        this.interfaceManager = crossplatForms.getInterfaceManager();
        this.bedrockRegistry = crossplatForms.getInterfaceManager().getBedrockRegistry();
        this.javaRegistry = crossplatForms.getInterfaceManager().getJavaRegistry();
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {

        // Base open command
        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .withSuggestionsProvider((context, s) -> interfaceSuggestions(context))
                        .build())
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    UUID uuid = origin.getUUID().orElseThrow();
                    Player player = serverHandler.getPlayer(uuid);
                    String identifier = context.get(ARGUMENT);
                    Interface ui = interfaceManager.getInterface(identifier, bedrockHandler.isBedrockPlayer(uuid));

                    if (ui == null) {
                        origin.sendMessage("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(ui.permission(Interface.Limit.COMMAND))) {
                        if (origin.hasPermission(ui.permission(Interface.Limit.USE))) {
                            ui.send(player);
                        } else {
                            origin.sendMessage("You don't have permission to use: " + identifier);
                        }
                    } else {
                        origin.sendMessage("You don't have permission to send: " + identifier);
                    }
                }));

        // Additional command to make other players open a form or menu
        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .withSuggestionsProvider((context, s) -> interfaceSuggestions(context))
                        .build())
                .argument(StringArgument.<CommandOrigin>newBuilder("player")
                        .withSuggestionsProvider(((context, s) -> playerSuggestions(context)))
                        .build())
                .permission(PERMISSION_OTHER)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    Player targetPlayer = serverHandler.getPlayer(target);
                    if (targetPlayer == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "The player " + target + " doesn't exist.");
                        return;
                    }
                    String identifier = context.get(ARGUMENT);
                    Interface ui = this.interfaceManager.getInterface(identifier, bedrockHandler.isBedrockPlayer(targetPlayer.getUuid()));
                    if (ui == null) {
                        origin.sendMessage("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(ui.permission(Interface.Limit.COMMAND))) {
                        if (targetPlayer.hasPermission(ui.permission(Interface.Limit.USE))) {
                            ui.send(targetPlayer);
                        } else {
                            origin.sendMessage(target + " doesn't have permission to use: " + identifier);
                        }
                    } else {
                        origin.sendMessage("You don't have permission to send: " + identifier);
                    }
                })
                .build()
        );
    }

    private List<String> interfaceSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList(); // BE players don't get argument suggestions
        }
        List<Interface> interfaces = new ArrayList<>();

        if (origin.hasPermission(PERMISSION_OTHER) && bedrockHandler.getPlayerCount() > 0) {
            // Permission to send to other players
            interfaces.addAll(bedrockRegistry.getForms().values());
        }
        interfaces.addAll(javaRegistry.getMenus().values());

        return interfaces.stream()
                .filter(ui -> origin.hasPermission(ui.permission(Interface.Limit.COMMAND)))
                .map(Interface::getIdentifier)
                .distinct() // Remove duplicates - forms and menus with the same identifier
                .collect(Collectors.toList());
    }

    private List<String> playerSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList(); // BE players don't get argument suggestions
        }
        String id = context.get(ARGUMENT);
        Interface bedrock = interfaceManager.getInterface(id, true);
        Interface java = interfaceManager.getInterface(id, false);

        // Don't suggest players if the form specified is not permissible
        if (bedrock != null && !origin.hasPermission(bedrock.permission(Interface.Limit.COMMAND))) {
            bedrock = null;
        }
        if (java != null && !origin.hasPermission(java.permission(Interface.Limit.COMMAND))) {
            java = null;
        }
        if (bedrock == null && java == null) {
            return Collections.emptyList(); // The form/menu specified doesnt exist or is not permissible
        }

        Interface bedrockInterface = bedrock; // must be effectively final for lambda
        Interface javaInterface = java;
        return serverHandler.getPlayers()
                .stream()
                .filter(player -> {
                    if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
                        return bedrockInterface != null && player.hasPermission(bedrockInterface.permission(Interface.Limit.USE));
                    } else {
                        return javaInterface != null && player.hasPermission(javaInterface.permission(Interface.Limit.USE));
                    }
                })
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
