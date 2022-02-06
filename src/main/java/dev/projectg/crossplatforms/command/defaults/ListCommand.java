package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

public class ListCommand extends FormsCommand {

    public static final String NAME = "list";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

    public ListCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        BedrockHandler bedrockHandler = crossplatForms.getBedrockHandler();
        BedrockFormRegistry bedrockRegistry = crossplatForms.getInterfaceManager().getBedrockRegistry();
        JavaMenuRegistry javaMenuRegistry = crossplatForms.getInterfaceManager().getJavaRegistry();

        manager.command(defaultBuilder.literal(NAME)
                .permission(PERMISSION)
                .handler(context -> {
                    context.getSender().sendMessage(Logger.Level.INFO, "Available forms/menus:");
                    String message = String.join(", ", OpenCommand.interfaceSuggestions(context, bedrockHandler, bedrockRegistry, javaMenuRegistry));
                    context.getSender().sendMessage(Logger.Level.INFO, message);
                    // todo: make it pretty
                })
                .build());
    }
}
