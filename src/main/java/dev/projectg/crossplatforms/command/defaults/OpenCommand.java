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
import dev.projectg.crossplatforms.interfacing.IntefaceRegistry;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        this.bedrockRegistry = crossplatForms.getIntefaceRegistry().getBedrockRegistry();
        this.javaRegistry = crossplatForms.getIntefaceRegistry().getJavaRegistry();
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        IntefaceRegistry intefaceRegistry = crossplatForms.getIntefaceRegistry();

        CommandArgument<CommandOrigin, String> formArgument = StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                .asRequired()
                .withSuggestionsProvider(this::interfaceSuggestions)
                .build();

        // Base open command
        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .asRequired()
                        .withSuggestionsProvider(this::interfaceSuggestions)
                        .build())
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .handler(context -> {
                    Player player = serverHandler.getPlayer(context.getSender().getUUID().orElseThrow());
                    // todo: check for command permission for this specific interface
                    intefaceRegistry.sendInterface(player, context.get(ARGUMENT));
                }));

        // Additional command to make other players open a form or menu
        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .withSuggestionsProvider(this::interfaceSuggestions)
                        .build())
                .argument(StringArgument.<CommandOrigin>newBuilder("player")
                        .withSuggestionsProvider(this::playerSuggestions)
                        .build())
                .permission(PERMISSION_OTHER)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    Player player = serverHandler.getPlayer(target);
                    // todo: check for permission for this specific interface
                    if (player == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "The player " + target + " doesn't exist!");
                    } else {
                        intefaceRegistry.sendInterface(player, context.get(ARGUMENT));
                    }
                })
                .build()
        );
    }

    private List<String> interfaceSuggestions(CommandContext<CommandOrigin> context, String s) {
        CommandOrigin origin = context.getSender();
        List<Interface> interfaces = new ArrayList<>();

        // If the player has permission to make other players open forms, don't filter the suggestions
        if (origin.isPlayer() && !origin.hasPermission(PERMISSION_OTHER)) {
            Player player = serverHandler.getPlayer(origin.getUUID().orElseThrow());
            boolean isBedrockPlayer = bedrockHandler.isBedrockPlayer(player.getUuid());

            if (isBedrockPlayer) {
                // Bedrock player
                interfaces.addAll(bedrockRegistry.getForms().values());
                for (JavaMenu menu : javaRegistry.getMenus().values()) {
                    // Add Java menus that the bedrock player has access to
                    if (menu.isAllowBedrock()) {
                        interfaces.add(menu);
                    }
                }
            } else {
                // Java player
                interfaces.addAll(javaRegistry.getMenus().values());
            }
        } else {
            // Console or command block, or a player with permission to send to other players
            if (bedrockHandler.getPlayerCount() > 0) {
                interfaces.addAll(bedrockRegistry.getForms().values());
            }
            interfaces.addAll(javaRegistry.getMenus().values());
        }

        return interfaces.stream()
                .filter(ui -> origin.hasPermission(ui.permission(Interface.Limit.COMMAND)))
                .map(Interface::getIdentifier)
                .distinct() // Remove duplicates - forms and menus with the same identifier
                .collect(Collectors.toList());
    }

    private List<String> playerSuggestions(CommandContext<CommandOrigin> context, String s) {
        CommandOrigin origin = context.getSender();
        String id = context.get(ARGUMENT);
        BedrockForm form = bedrockRegistry.getForm(id);
        JavaMenu menu = javaRegistry.getMenus().get(id);

        // Don't suggest players if the form specified is not permissible
        if (form != null && !origin.hasPermission(form.permission(Interface.Limit.COMMAND))) {
            form = null;
        }
        if (menu != null && !origin.hasPermission(menu.permission(Interface.Limit.COMMAND))) {
            menu = null;
        }

        if (form == null && menu == null) {
            return Collections.emptyList(); // The form/menu specified doesnt exist or is not permissible
        }

        // Used for bedrock players below when checking perms below
        Interface ui = Objects.requireNonNullElse(form, menu);
        // Different conditions to list JE and BE players
        boolean bedrockCondition = form != null || menu.isAllowBedrock();
        boolean javaCondition = menu != null;

        JavaMenu finalMenu = menu; // must be effectively final
        return serverHandler.getPlayers()
                .stream()
                .filter(player -> {
                    // Only show players that can receive the inputted form/menu
                    if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
                        return bedrockCondition && player.hasPermission(ui.permission(Interface.Limit.USE));
                    } else {
                        return javaCondition && player.hasPermission(finalMenu.permission(Interface.Limit.USE));
                    }
                })
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
