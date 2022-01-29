package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import com.google.common.collect.ImmutableList;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.JavaMenuRegistry;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class DefaultCommands {

    @Getter
    private final List<FormsCommand> commands;

    public DefaultCommands(CrossplatForms forms, MinecraftHelp<CommandOrigin> minecraftHelp) {

        BedrockFormRegistry formRegistry = forms.getInterfaceManager().getBedrockRegistry();
        JavaMenuRegistry menuRegistry = forms.getInterfaceManager().getJavaRegistry();

        // todo: move this code into a suggestion provider in a custom command argument for interfaces
        BiFunction<CommandContext<CommandOrigin>, String, List<String>> suggestionProvider = (context, string) -> {
            List<String> suggestions = new ArrayList<>();

            CommandOrigin origin = context.getSender();
            if (origin.isPlayer()) {
                if (forms.getBedrockHandler().isBedrockPlayer(origin.getUUID().orElseThrow())) {
                    addBedrockForms(formRegistry, origin, suggestions);
                } else {
                    addJavaMenus(menuRegistry, origin, suggestions);
                }
            } else {
                // todo can improve this
                addBedrockForms(formRegistry, origin, suggestions);
                addJavaMenus(menuRegistry, origin, suggestions);
            }

            return suggestions;
        };

        commands = ImmutableList.of(
                new HelpCommand(forms, minecraftHelp),
                new ListCommand(forms),
                new OpenCommand(forms),
                new InspectCommand(forms),
                new IdentifyCommand(forms),
                new VersionCommand(forms),
                new ReloadCommand(forms)
        );
    }


    private static void addBedrockForms(BedrockFormRegistry registry, CommandOrigin origin, List<String> list) {
        Map<String, BedrockForm> forms = registry.getForms();
        for (String name : forms.keySet()) {
            if (origin.hasPermission(forms.get(name).permission(Interface.Limit.COMMAND))) {
                list.add(name);
            }
        }
    }

    private static void addJavaMenus(JavaMenuRegistry registry, CommandOrigin origin, List<String> list) {
        Map<String, JavaMenu> menus = registry.getMenus();
        for (String name : menus.keySet()) {
            if (origin.hasPermission(menus.get(name).permission(Interface.Limit.COMMAND))) {
                list.add(name);
            }
        }
    }
}
