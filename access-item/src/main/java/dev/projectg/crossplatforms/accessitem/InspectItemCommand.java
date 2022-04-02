package dev.projectg.crossplatforms.accessitem;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.command.defaults.InspectCommand;

import java.util.stream.Collectors;

public class InspectItemCommand extends FormsCommand {

    private final AccessItemRegistry itemRegistry;

    public InspectItemCommand(CrossplatForms crossplatForms, AccessItemRegistry itemRegistry) {
        super(crossplatForms);
        this.itemRegistry = itemRegistry;
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        // addon to the inspect command
        manager.command(crossplatForms.getCommandBuilder()
                .literal(InspectCommand.NAME)
                .permission(InspectCommand.PERMISSION)
                .literal("item")
                .argument(StringArgument.<CommandOrigin>newBuilder("item")
                        .withSuggestionsProvider(((context, s) -> itemRegistry.getItems().values()
                                .stream()
                                .map(AccessItem::getIdentifier)
                                .collect(Collectors.toList()))))
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String name = context.get("item");
                    AccessItem item = itemRegistry.getItems().get(name);
                    if (item == null) {
                        origin.warn("That Access Item doesn't exist!");
                    } else {
                        origin.sendMessage("Inspection of access item: " + name);
                        origin.sendMessage(item.toString());
                    }
                })
        );
    }
}
