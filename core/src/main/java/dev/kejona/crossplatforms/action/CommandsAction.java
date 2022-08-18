package dev.kejona.crossplatforms.action;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.TypeResolver;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class CommandsAction implements Action<Object> {

    public static final String TYPE = "commands";

    private final transient ServerHandler serverHandler;

    @Required
    private List<DispatchableCommand> commands;

    @Inject
    public CommandsAction(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Resolver resolver, @Nonnull Object source) {
        if (commands != null) {
            List<DispatchableCommand> resolved = commands.stream()
                    .map(cmd -> cmd.withCommand(resolver.apply(cmd.getCommand())))
                    .collect(Collectors.toList());

            serverHandler.dispatchCommands(player.getUuid(), resolved);
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
        serializer.register(TYPE, CommandsAction.class, typeResolver());
    }

    private static TypeResolver typeResolver() {
        return TypeResolver.listOrScalar("commands", TYPE);
    }
}

