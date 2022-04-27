package dev.projectg.crossplatforms.action;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigSerializable
public class CommandsAction extends SimpleAction<List<DispatchableCommand>> {

    public static final String TYPE = "commands";

    @Inject
    private transient ServerHandler serverHandler;

    @Inject
    private transient PlaceholderHandler placeholders;

    @Inject
    public CommandsAction(List<DispatchableCommand> commands) {
        super(TYPE, commands);
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        List<DispatchableCommand> resolved = value().stream()
                .map(command -> command.withCommand(placeholders.setPlaceholders(player, command.getCommand(), additionalPlaceholders)))
                .collect(Collectors.toList());

        serverHandler.dispatchCommands(player.getUuid(), resolved);
    }
}

