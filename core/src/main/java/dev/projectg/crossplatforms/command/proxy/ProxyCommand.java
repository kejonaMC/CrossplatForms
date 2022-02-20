package dev.projectg.crossplatforms.command.proxy;

import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.Player;
import dev.projectg.crossplatforms.interfacing.BasicClickAction;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nullable;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ProxyCommand {

    @NodeKey
    private String name = null;

    @Required
    private CommandType method = null;

    @Nullable
    private BasicClickAction action = null;

    @Nullable
    private BasicClickAction bedrockAction = null;

    @Nullable
    private BasicClickAction javaAction = null;

    @Setter
    private String permission;

    public void run(Player player, InterfaceManager interfaceManager, BedrockHandler bedrockHandler) {
        if (action != null) {
            action.affectPlayer(player, interfaceManager, bedrockHandler);
        }
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (bedrockAction != null) {
                bedrockAction.affectPlayer(player, interfaceManager, bedrockHandler);
            }
        } else {
            if (javaAction != null) {
                javaAction.affectPlayer(player, interfaceManager, bedrockHandler);
            }
        }
    }
}
