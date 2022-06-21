package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.minecraft.extras.MinecraftHelp;
import com.google.common.collect.ImmutableList;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import lombok.Getter;

import java.util.List;

public final class DefaultCommands {

    @Getter
    private final List<FormsCommand> commands;

    public DefaultCommands(CrossplatForms instance, MinecraftHelp<CommandOrigin> minecraftHelp) {

        commands = ImmutableList.of(
                new HelpCommand(instance, minecraftHelp),
                new ListCommand(instance),
                new OpenCommand(instance),
                new InspectCommand(instance),
                new IdentifyCommand(instance),
                new VersionCommand(instance),
                new ReloadCommand(instance)
        );
    }
}
