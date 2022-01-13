package dev.projectg.crossplatforms.command.defaults;

import com.google.common.collect.ImmutableList;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.form.AccessItemRegistry;
import dev.projectg.crossplatforms.form.bedrock.BedrockFormRegistry;
import dev.projectg.crossplatforms.form.java.JavaMenuRegistry;
import dev.projectg.crossplatforms.handler.bedrock.BedrockHandler;
import dev.projectg.crossplatforms.handler.server.ServerHandler;
import lombok.Getter;

import java.util.List;

public final class DefaultCommands {

    @Getter
    private final List<FormsCommand> commands;

    public DefaultCommands(CrossplatForms forms) {

        BedrockHandler bedrockHandler = forms.getBedrockHandler();
        ServerHandler serverHandler = forms.getServerHandler();

        BedrockFormRegistry bedrockFormRegistry = forms.getBedrockFormRegistry();
        JavaMenuRegistry javaMenuRegistry = forms.getJavaMenuRegistry();
        AccessItemRegistry accessItemRegistry = forms.getAccessItemRegistry();


        commands = ImmutableList.of(
                new HelpCommand(),
                new ListCommand(),
                new OpenCommand(
                        serverHandler,
                        bedrockFormRegistry,
                        javaMenuRegistry),
                new InspectCommand(bedrockFormRegistry, javaMenuRegistry, accessItemRegistry),
                new IdentifyCommand(
                        serverHandler,
                        bedrockHandler),
                new VersionCommand(forms),
                new ReloadCommand(forms)
        );
    }
}
