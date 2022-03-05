package dev.projectg.crossplatforms.action;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigSerializable
public class CommandsAction extends SimpleAction<List<DispatchableCommand>> {

    public static final String IDENTIFIER = "commands";

    public CommandsAction(List<DispatchableCommand> commands) {
        super(commands);
    }

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders, @Nonnull InterfaceManager interfaceManager, @Nonnull BedrockHandler bedrockHandler) {
        PlaceholderHandler placeholders = CrossplatForms.getInstance().getPlaceholders();
        List<DispatchableCommand> resolved = value().stream()
                .map(command -> command.withCommand(placeholders.setPlaceholders(player, command.command(), additionalPlaceholders)))
                .collect(Collectors.toList());

        CrossplatForms.getInstance().getServerHandler().dispatchCommands(player.getUuid(), resolved);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

