package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        JavaMenuRegistry javaRegistry = crossplatForms.getInterfaceManager().getJavaRegistry();

        manager.command(defaultBuilder.literal(NAME)
                .permission(PERMISSION)
                .handler(context -> {
                    List<Interface> interfaces = new ArrayList<>();
                    CommandOrigin origin = context.getSender();
                    if (origin.isPlayer() && !origin.hasPermission(OpenCommand.PERMISSION_OTHER)) {
                        if (bedrockHandler.isBedrockPlayer(origin.getUUID().orElseThrow())) {
                            interfaces.addAll(bedrockRegistry.getForms().values());
                            javaRegistry.getMenus().values().stream().filter(JavaMenu::isAllowBedrock).forEach(interfaces::add);
                        } else {
                            interfaces.addAll(javaRegistry.getMenus().values());
                        }
                    } else {
                        // Origin is console or they have permission to send to others
                        interfaces.addAll(bedrockRegistry.getForms().values());
                        interfaces.addAll(javaRegistry.getMenus().values());
                    }

                    List<String> names = interfaces.stream()
                            .filter(ui -> origin.hasPermission(ui.permission(Interface.Limit.COMMAND)))
                            .map(Interface::getIdentifier)
                            .distinct() // Remove duplicates - forms and menus with the same identifier
                            .collect(Collectors.toList());

                    if (names.isEmpty()) {
                        context.getSender().sendMessage(Logger.Level.INFO, "There are no menus or forms to list.");
                    } else {
                        context.getSender().sendMessage(Logger.Level.INFO, "Available forms/menus:");
                        context.getSender().sendMessage(Logger.Level.INFO, String.join(", ", names));
                    }
                })
                .build());
    }
}
