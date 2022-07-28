package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.serialize.AsNodePath;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigSerializable
public class CommandsAction implements Action {

    private static final String TYPE = "commands";

    private final transient ServerHandler serverHandler;
    private final transient Placeholders placeholders;

    @AsNodePath
    private String nodePath;
    private List<DispatchableCommand> commands;
    private DispatchableCommand command;

    @Inject
    public CommandsAction(ServerHandler serverHandler, Placeholders placeholders) {
        this.serverHandler = serverHandler;
        this.placeholders = placeholders;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        if (commands != null) {
            List<DispatchableCommand> resolved = commands.stream()
                    .map(command -> command.withCommand(placeholders.setPlaceholders(player, command.getCommand(), additionalPlaceholders)))
                    .collect(Collectors.toList());

            serverHandler.dispatchCommands(player.getUuid(), resolved);
        }
        if (command != null) {
            String resolved = placeholders.setPlaceholders(player, command.getCommand(), additionalPlaceholders);
            serverHandler.dispatchCommand(player.getUuid(), command.withCommand(resolved));
        }
    }

    @PostProcess
    private void postProcess() throws SerializationException {
        if (commands == null && command == null) {
            throw new SerializationException("Commands action must have either 'command' or 'commands'");
        }
        if (commands != null && command != null) {
            Logger.get().warn(nodePath + " has both 'commands' and 'command'");
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public boolean serializeWithType() {
        return false; // can infer based off commands node
    }

    public static void register(ActionSerializer serializer) {
        serializer.genericAction(TYPE, CommandsAction.class, typeResolver());
    }

    private static TypeResolver typeResolver() {
        return node -> {
            if (node.node("commands").isList()) {
                return TYPE;
            } else {
                return null;
            }
        };
    }
}

