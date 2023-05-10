package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.kejona.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.interfacing.java.JavaMenuRegistry;

import java.util.stream.Collectors;

public class InspectCommand extends FormsCommand {

    public static final String NAME = "inspect";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

    public InspectCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        BedrockFormRegistry bedrockRegistry = crossplatForms.getInterfacer().getBedrockRegistry();
        JavaMenuRegistry javaRegistry = crossplatForms.getInterfacer().getJavaRegistry();

        Command.Builder<CommandOrigin> base = defaultBuilder
                .literal(NAME)
                .permission(PERMISSION);

        manager.command(base
                .literal("form")
                .argument(StringArgument.<CommandOrigin>builder("form")
                        .withSuggestionsProvider(((context, s) -> bedrockRegistry.getForms().values()
                                .stream()
                                .map(Interface::getIdentifier)
                                .collect(Collectors.toList()))))
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String name = context.get("form");
                    BedrockForm form = bedrockRegistry.getForm(name);
                    if (form == null) {
                        origin.warn("That form doesn't exist!");
                    } else {
                        origin.sendMessage("Inspection of form: " + name);
                        origin.sendMessage(form.toString());
                    }
                })
        );

        manager.command(base
                .literal("menu")
                .argument(StringArgument.<CommandOrigin>builder("menu")
                        .withSuggestionsProvider(((context, s) -> javaRegistry.getMenus().values()
                                .stream()
                                .map(Interface::getIdentifier)
                                .collect(Collectors.toList()))))
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String name = context.get("menu");
                    JavaMenu menu = javaRegistry.getMenu(name);
                    if (menu == null) {
                        origin.warn("That menu doesn't exist!");
                    } else {
                        origin.sendMessage("Inspection of menu: " + name);
                        origin.sendMessage(CrossplatForms.PLAIN_SERIALIZER.deserialize(menu.toString()));
                    }
                })
        );
    }
}
