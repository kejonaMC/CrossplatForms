package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.minecraft.extras.MinecraftHelp;
import com.google.common.collect.ImmutableList;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
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
                new GiveCommand(instance),
                new InspectCommand(instance),
                new IdentifyCommand(instance),
                new VersionCommand(instance),
                new ReloadCommand(instance)
        );
    }
}
