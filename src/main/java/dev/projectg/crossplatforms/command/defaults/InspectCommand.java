package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.form.AccessItem;
import dev.projectg.crossplatforms.form.AccessItemRegistry;
import dev.projectg.crossplatforms.form.bedrock.BedrockForm;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.form.java.JavaMenu;
import dev.projectg.crossplatforms.form.java.JavaMenuRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InspectCommand implements FormsCommand {

    private static final String NAME = "inspect";
    private static final String PERMISSION = "crossplatforms.inspect";

    private final BedrockFormRegistry bedrockRegistry;
    private final JavaMenuRegistry javaRegistry;
    private final AccessItemRegistry accessItemRegistry;

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
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
                    AccessItem item = accessItemRegistry.getAccessItems().get(name);
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
