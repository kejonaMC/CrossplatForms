package dev.projectg.crossplatforms.spigot;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.item.AccessItem;
import dev.projectg.crossplatforms.item.AccessItemListeners;
import dev.projectg.crossplatforms.item.AccessItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GiveCommand extends FormsCommand {

    public static final String NAME = "give";
    public static final String PERMISSION = PERMISSION_BASE + NAME;
    public static final String PERMISSION_OTHER = PERMISSION + ".others";
    private static final String ARGUMENT = "accessitem";

    private final BedrockHandler bedrockHandler;
    private final ServerHandler serverHandler;
    private final AccessItemRegistry itemRegistry;

    public GiveCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
        bedrockHandler = crossplatForms.getBedrockHandler();
        serverHandler = crossplatForms.getServerHandler();
        itemRegistry = crossplatForms.getAccessItemRegistry();
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
                    UUID uuid = origin.getUUID().orElseThrow();
                    Player player = Bukkit.getPlayer(uuid);
                    String identifier = context.get(ARGUMENT);
                    AccessItem item = itemRegistry.getItems().get(identifier);

                    if (item == null) {
                        origin.sendMessage("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(item.permission(AccessItem.Limit.COMMAND))) {
                        if (origin.hasPermission(item.permission(AccessItem.Limit.POSSESS))) {
                            if (!AccessItemListeners.giveAccessItem(player, item, false)) {
                                origin.sendMessage("Your inventory is too full!");
                            }
                        } else {
                            origin.sendMessage("You don't have permission to have: " + identifier);
                        }
                    } else {
                        origin.sendMessage("You don't have permission to give: " + identifier);
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
                        origin.sendMessage(Logger.Level.SEVERE, "The player " + target + " doesn't exist.");
                        return;
                    }
                    String identifier = context.get(ARGUMENT);
                    AccessItem item = itemRegistry.getItems().get(identifier);
                    if (item == null) {
                        origin.sendMessage("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(item.permission(AccessItem.Limit.COMMAND))) {
                        if (targetPlayer.hasPermission(item.permission(AccessItem.Limit.POSSESS))) {
                            if (!AccessItemListeners.giveAccessItem((Player) targetPlayer.getHandle(), item, false)) {
                                origin.sendMessage(String.format("%s's inventory is too full!", targetPlayer.getName()));
                            }
                        } else {
                            origin.sendMessage(target + " doesn't have permission to have: " + identifier);
                        }
                    } else {
                        origin.sendMessage("You don't have permission to give: " + identifier);
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
                .stream()
                .filter(player -> player.hasPermission(item.permission(AccessItem.Limit.POSSESS)))
                .map(FormPlayer::getName)
                .collect(Collectors.toList());
    }
}
