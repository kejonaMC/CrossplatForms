package dev.projectg.crossplatforms.command.custom;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Platform;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.serialize.ValuedType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Collections;
import java.util.List;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class CustomCommand implements ValuedType {

    @Inject
    private transient BedrockHandler bedrockHandler;

    @Required
    @NodeKey
    private String identifier = null;

    private Platform platform = Platform.ALL;

    @Required
    private CommandType method = null;

    private List<Action> actions = Collections.emptyList();
    private List<Action> bedrockActions = Collections.emptyList();
    private List<Action> javaActions = Collections.emptyList();

    @Setter
    private String permission;

    public void run(FormPlayer player) {
        Action.affectPlayer(player, actions);

        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            Action.affectPlayer(player, bedrockActions);
        } else {
            Action.affectPlayer(player, javaActions);
        }
    }
}
