package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

import java.util.stream.Collectors;

public class InspectCommand extends FormsCommand {

    public static final String NAME = "inspect";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

    public InspectCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        ServerHandler serverHandler = crossplatForms.getServerHandler();
        BedrockFormRegistry bedrockRegistry = crossplatForms.getInterfaceManager().getBedrockRegistry();
        JavaMenuRegistry javaRegistry = crossplatForms.getInterfaceManager().getJavaRegistry();

        Command.Builder<CommandOrigin> base = defaultBuilder
                .literal(NAME)
                .permission(PERMISSION);

        manager.command(base
                .literal("form")
                .argument(StringArgument.<CommandOrigin>newBuilder("form")
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
                .argument(StringArgument.<CommandOrigin>newBuilder("menu")
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
