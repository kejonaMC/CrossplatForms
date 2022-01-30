package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.item.AccessItem;
import dev.projectg.crossplatforms.item.AccessItemRegistry;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

public class InspectCommand extends FormsCommand {

    private static final String NAME = "inspect";
    private static final String PERMISSION = PERMISSION_BASE + NAME;

    public InspectCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        BedrockFormRegistry bedrockRegistry = crossplatForms.getInterfaceManager().getBedrockRegistry();
        JavaMenuRegistry javaRegistry = crossplatForms.getInterfaceManager().getJavaRegistry();
        AccessItemRegistry accessItemRegistry = crossplatForms.getAccessItemRegistry();

        Command.Builder<CommandOrigin> base = defaultBuilder
                .literal(NAME)
                .permission(PERMISSION);

        manager.command(base
                .literal("form")
                .argument(StringArgument.of("form"))
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String name = context.get("form");
                    BedrockForm form = bedrockRegistry.getForm(name);
                    if (form == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "That form doesn't exist!");
                    } else {
                        origin.sendMessage(Logger.Level.INFO, "Inspection of form" + name);
                        origin.sendMessage(Logger.Level.INFO, form.toString());
                    }
                })
        );

        manager.command(base
                .literal("menu")
                .argument(StringArgument.of("menu"))
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String name = context.get("menu");
                    JavaMenu menu = javaRegistry.getMenu(name);
                    if (menu == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "That menu doesn't exist!");
                    } else {
                        origin.sendMessage(Logger.Level.INFO, "Inspection of menu" + name);
                        origin.sendMessage(Logger.Level.INFO, menu.toString());
                    }
                })
        );

        manager.command(base
                .literal("item")
                .argument(StringArgument.of("item"))
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String name = context.get("item");
                    AccessItem item = accessItemRegistry.getItems().get(name);
                    if (item == null) {
                        origin.sendMessage(Logger.Level.SEVERE, "That Access Item doesn't exist!");
                    } else {
                        origin.sendMessage(Logger.Level.INFO, "Inspection of access item" + name);
                        origin.sendMessage(Logger.Level.INFO, item.toString());
                    }
                })
        );
    }
}
