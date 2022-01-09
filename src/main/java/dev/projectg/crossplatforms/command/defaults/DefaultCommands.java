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
                new HelpCommand(),
                new ListCommand(),
                new OpenCommand(
                        forms.getServerHandler(),
                        forms.getBedrockFormRegistry(),
                        forms.getJavaMenuRegistry()),
                new IdentifyCommand(
                        forms.getServerHandler(),
                        forms.getBedrockHandler()),
                new VersionCommand(forms),
                new ReloadCommand(forms)
        );
    }
}
