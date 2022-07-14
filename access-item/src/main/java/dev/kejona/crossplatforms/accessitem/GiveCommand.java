package dev.kejona.crossplatforms.accessitem;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class GiveCommand extends FormsCommand {

    public static final String NAME = "give";
    public static final String PERMISSION = PERMISSION_BASE + NAME;
    public static final String PERMISSION_OTHER = PERMISSION + ".others";
    private static final String ARGUMENT = "accessitem";

    private final BedrockHandler bedrockHandler;
    private final ServerHandler serverHandler;
    private final AccessItemRegistry itemRegistry;

    public GiveCommand(CrossplatForms crossplatForms, AccessItemRegistry itemRegistry) {
        super(crossplatForms);
        this.bedrockHandler = crossplatForms.getBedrockHandler();
        this.serverHandler = crossplatForms.getServerHandler();
        this.itemRegistry = itemRegistry;
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {


        // Base open command
        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .withSuggestionsProvider((context, s) -> itemSuggestions(context))
                        .build())
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    FormPlayer player = serverHandler.getPlayer(origin.getUUID().orElseThrow(AssertionError::new));
                    String identifier = context.get(ARGUMENT);
                    AccessItem item = itemRegistry.getItems().get(identifier);

                    if (item == null) {
                        origin.warn("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(item.permission(AccessItem.Limit.COMMAND))) {
                        if (origin.hasPermission(item.permission(AccessItem.Limit.POSSESS))) {
                            if (!itemRegistry.giveAccessItem(player, item, false)) {
                                origin.warn("Your inventory is too full!");
                            }
                        } else {
                            origin.warn("You don't have permission to have: " + identifier);
                        }
                    } else {
                        origin.warn("You don't have permission to give: " + identifier);
                    }
                }));

        // Additional command to make other players open a form or menu
        manager.command(defaultBuilder
                .literal(NAME)
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .withSuggestionsProvider((context, s) -> itemSuggestions(context))
                        .build())
                .argument(StringArgument.<CommandOrigin>newBuilder("player")
                        .withSuggestionsProvider(((context, s) -> playerSuggestions(context)))
                        .build())
                .permission(PERMISSION_OTHER)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    FormPlayer targetPlayer = serverHandler.getPlayer(target);
                    if (targetPlayer == null) {
                        origin.warn("The player " + target + " doesn't exist.");
                        return;
                    }
                    String identifier = context.get(ARGUMENT);
                    AccessItem item = itemRegistry.getItems().get(identifier);
                    if (item == null) {
                        origin.warn("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(item.permission(AccessItem.Limit.COMMAND))) {
                        if (targetPlayer.hasPermission(item.permission(AccessItem.Limit.POSSESS))) {
                            if (!itemRegistry.giveAccessItem(targetPlayer, item, false)) {
                                origin.warn(String.format("%s's inventory is too full!", targetPlayer.getName()));
                            }
                        } else {
                            origin.warn(target + " doesn't have permission to have: " + identifier);
                        }
                    } else {
                        origin.warn("You don't have permission to give: " + identifier);
                    }
                })
                .build()
        );
    }

    private List<String> itemSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList(); // BE players don't get argument suggestions
        }

        return itemRegistry.getItems().values().stream()
                .filter(item -> origin.hasPermission(item.permission(AccessItem.Limit.COMMAND)))
                .map(AccessItem::getIdentifier)
                .collect(Collectors.toList());
    }

    private List<String> playerSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList(); // BE players don't get argument suggestions
        }
        String id = context.get(ARGUMENT);
        AccessItem item = itemRegistry.getItems().get(id);
        if (item == null) {
            return Collections.emptyList();
        }

        return serverHandler.getPlayers()
            .filter(player -> player.hasPermission(item.permission(AccessItem.Limit.POSSESS)))
            .map(FormPlayer::getName)
            .collect(Collectors.toList());
    }
}
