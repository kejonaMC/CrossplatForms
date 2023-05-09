package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Argument;
import dev.kejona.crossplatforms.interfacing.ArgumentException;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenuRegistry;
import dev.kejona.crossplatforms.parser.BlockPlaceholderParser;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OpenCommand extends FormsCommand {

    public static final String OPEN_NAME = "open";
    public static final String SEND_NAME = "send";
    public static final String PERMISSION = PERMISSION_BASE + OPEN_NAME;
    public static final String PERMISSION_OTHER = PERMISSION_BASE + SEND_NAME;
    private static final String PLAYER_ARG = "player";
    private static final String INTERFACE_ARG = "form|menu";
    private static final String EXTRAS_ARG = "extras";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;
    private final Interfacer interfacer;
    private final JavaMenuRegistry javaRegistry;

    private final String openCommand;
    private final String sendCommand;

    public OpenCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);

        this.serverHandler = crossplatForms.getServerHandler();
        this.bedrockHandler = crossplatForms.getBedrockHandler();
        this.interfacer = crossplatForms.getInterfacer();
        this.javaRegistry = crossplatForms.getInterfacer().getJavaRegistry();

        String root = crossplatForms.getRootCommand();
        openCommand = join(root, OPEN_NAME);
        sendCommand = join(root, SEND_NAME, required(PLAYER_ARG));
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {

        // Base open command
        manager.command(defaultBuilder
                .literal(OPEN_NAME)
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .argument(StringArgument.<CommandOrigin>builder(INTERFACE_ARG)
                        .withSuggestionsProvider((context, s) -> openSuggestions(context))
                        .build())
                .argument(extrasArgument())
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    UUID uuid = origin.getUUID().orElseThrow(AssertionError::new);
                    String identifier = context.get(INTERFACE_ARG);
                    Interface ui = interfacer.getInterface(identifier, bedrockHandler.isBedrockPlayer(uuid));

                    if (ui == null) {
                        origin.warn("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(ui.permission(Interface.Limit.COMMAND))) {
                        if (origin.hasPermission(ui.permission(Interface.Limit.USE))) {
                            FormPlayer player = Objects.requireNonNull(serverHandler.getPlayer(uuid));
                            send(openCommand, context, ui, player);
                        } else {
                            origin.warn("You don't have permission to use: " + identifier);
                        }
                    } else {
                        origin.warn("You don't have permission to send: " + identifier);
                    }
                })
        );

        // Send command to make other players open a form or menu
        manager.command(defaultBuilder
                .literal(SEND_NAME)
                .permission(PERMISSION_OTHER)
                .argument(StringArgument.<CommandOrigin>builder("player")
                        .withSuggestionsProvider((context, s) -> serverHandler.getPlayers()
                                .map(FormPlayer::getName)
                                .collect(Collectors.toList()))
                        .build())
                .argument(StringArgument.<CommandOrigin>builder(INTERFACE_ARG)
                        .withSuggestionsProvider((context, s) -> sendSuggestions(context))
                        .build())
                .argument(extrasArgument())
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    FormPlayer targetPlayer = serverHandler.getPlayer(target);
                    if (targetPlayer == null) {
                        origin.warn("The player " + target + " doesn't exist.");
                        return;
                    }
                    String identifier = context.get(INTERFACE_ARG);
                    Interface ui = interfacer.getInterface(identifier, bedrockHandler.isBedrockPlayer(targetPlayer.getUuid()));
                    if (ui == null) {
                        origin.warn("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(ui.permission(Interface.Limit.COMMAND))) {
                        if (targetPlayer.hasPermission(ui.permission(Interface.Limit.USE))) {
                            send(sendCommand, context, ui, targetPlayer);
                        } else {
                            origin.warn(target + " doesn't have permission to use: " + identifier);
                        }
                    } else {
                        origin.warn("You don't have permission to send: " + identifier);
                    }
                })
        );
    }

    private CommandArgument<CommandOrigin, String> extrasArgument() {
        return StringArgument.optional(EXTRAS_ARG, StringArgument.StringMode.GREEDY);
    }

    private void send(String command, CommandContext<CommandOrigin> ctx, Interface ui, FormPlayer recipient) {
        List<Argument> parameters = ui.getArguments();
        if (parameters.isEmpty()) {
            send(ui, ctx.getSender(), recipient);
            return;
        }

        CommandOrigin origin = ctx.getSender();
        Optional<String> input = ctx.getOptional(EXTRAS_ARG);

        if (input.isPresent()) {
            // It's probably possible for placeholders to be eventually parsed, so this is a safeguard
            String[] args = BlockPlaceholderParser.block(input.get()).split(" ");
            if (args.length != parameters.size()) {
                badSyntax(origin, command, ui);
                return;
            }

            send(ui, origin, recipient, args);
        } else {
            badSyntax(origin, command, ui);
        }
    }

    private void send(Interface ui, CommandOrigin origin, FormPlayer recipient, @Nullable String... args) {
        try {
            ui.send(recipient, args);
        } catch (ArgumentException e) {
            origin.warn("Failed to open " + ui.getIdentifier() + ": " + e.getMessage());
            badSyntax(origin, openCommand, ui);
        }
    }

    private void badSyntax(CommandOrigin origin, String command, Interface ui) {
        origin.warn("The correct syntax is: " + command + " " + ui.getArgumentSyntax());
    }

    private List<String> openSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList(); // BE players don't get argument suggestions
        }

        return javaRegistry.getMenus().values().stream()
                .filter(menu -> origin.hasPermission(menu.permission(Interface.Limit.COMMAND)))
                .map(Interface::getIdentifier)
                .collect(Collectors.toList());
    }

    private List<String> sendSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList();
        }

        String recipient = context.get("player");
        FormPlayer target = serverHandler.getPlayer(recipient);
        if (target == null) {
            return Collections.emptyList();
        }

        return interfacer.getInterfaces(bedrockHandler.isBedrockPlayer(target.getUuid()))
                .stream()
                .filter(ui -> origin.hasPermission(ui.permission(Interface.Limit.COMMAND)) && target.hasPermission(ui.permission(Interface.Limit.USE)))
                .map(Interface::getIdentifier)
                .distinct() // Remove duplicates - forms and menus with the same identifier
                .collect(Collectors.toList());
    }

    private static String join(String... args) {
        return String.join(" ", args);
    }

    public static String required(String argument) {
        return "<" + argument + ">";
    }

    public static String optional(String argument) {
        return "[" + argument + "]";
    }
}
