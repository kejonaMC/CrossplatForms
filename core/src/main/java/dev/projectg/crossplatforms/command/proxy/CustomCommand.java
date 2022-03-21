package dev.projectg.crossplatforms.command.proxy;

import dev.projectg.crossplatforms.Platform;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Collections;
import java.util.List;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class CustomCommand {

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

    public void run(FormPlayer player, InterfaceManager interfaceManager, BedrockHandler bedrockHandler) {
        Action.affectPlayer(player, actions, interfaceManager, bedrockHandler);

        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            Action.affectPlayer(player, bedrockActions, interfaceManager, bedrockHandler);
        } else {
            Action.affectPlayer(player, javaActions, interfaceManager, bedrockHandler);
        }
    }
}
