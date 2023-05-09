package dev.kejona.crossplatforms.command.custom;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Platform;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.command.CommandType;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.KeyedType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Collections;
import java.util.List;

@ToString
@Getter
@SuppressWarnings("FieldMayBeFinal")
public abstract class CustomCommand implements KeyedType {

    @Inject
    private transient BedrockHandler bedrockHandler;

    @Inject
    private transient Placeholders placeholders;

    @Required
    @NodeKey
    private String identifier = null;

    private Platform platform = Platform.ALL;

    @Required
    private CommandType method = null;

    private List<Action<? super CustomCommand>> actions = Collections.emptyList();
    private List<Action<? super CustomCommand>> bedrockActions = Collections.emptyList();
    private List<Action<? super CustomCommand>> javaActions = Collections.emptyList();

    @Setter
    private String permission;

    public void run(FormPlayer player) {
        Resolver resolver = placeholders.resolver(player);
        Action.affectPlayer(player, actions, resolver, this);

        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            Action.affectPlayer(player, bedrockActions, resolver, this);
        } else {
            Action.affectPlayer(player, javaActions, resolver, this);
        }
    }
}
