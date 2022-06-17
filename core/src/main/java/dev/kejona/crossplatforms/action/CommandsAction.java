package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandsAction extends SimpleAction<List<DispatchableCommand>> {

    public static final String TYPE = "commands";

    private final transient ServerHandler serverHandler;
    private final transient Placeholders placeholders;

    @Inject
    public CommandsAction(List<DispatchableCommand> commands, ServerHandler serverHandler, Placeholders placeholders) {
        super(TYPE, commands);
        this.serverHandler = serverHandler;
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        List<DispatchableCommand> resolved = value().stream()
                .map(command -> command.withCommand(placeholders.setPlaceholders(player, command.getCommand(), additionalPlaceholders)))
                .collect(Collectors.toList());

        serverHandler.dispatchCommands(player.getUuid(), resolved);
    }
}

