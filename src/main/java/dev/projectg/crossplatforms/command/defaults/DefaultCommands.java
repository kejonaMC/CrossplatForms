package dev.projectg.crossplatforms.command.defaults;

import com.google.common.collect.ImmutableList;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.FormsCommand;
import lombok.Getter;

import java.util.List;

public final class DefaultCommands {

    @Getter
    private final List<FormsCommand> commands;

    public DefaultCommands(CrossplatForms forms) {

        commands = ImmutableList.of(
                new HelpCommand(forms),
                new ListCommand(forms),
                new OpenCommand(forms),
                new InspectCommand(forms),
                new IdentifyCommand(forms),
                new VersionCommand(forms),
                new ReloadCommand(forms)
        );
    }
}
