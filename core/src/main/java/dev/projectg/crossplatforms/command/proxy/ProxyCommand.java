package dev.projectg.crossplatforms.command.proxy;

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

import java.util.List;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ProxyCommand {

    @NodeKey
    private String name = null;

    @Required
    private CommandType method = null;

    private List<Action> actions = null;
    private List<Action> bedrockActions = null;
    private List<Action> javaActions = null;

    @Setter
    private String permission;

    public void run(FormPlayer player, InterfaceManager interfaceManager, BedrockHandler bedrockHandler) {
        if (actions != null) {
            for (Action action : actions) {
                action.affectPlayer(player, interfaceManager, bedrockHandler);
            }
        }
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (bedrockActions != null) {
                for (Action action : bedrockActions) {
                    action.affectPlayer(player, interfaceManager, bedrockHandler);
                }
            }
        } else {
            if (javaActions != null) {
                for (Action action : javaActions) {
                    action.affectPlayer(player, interfaceManager, bedrockHandler);
                }
            }
        }
    }
}
