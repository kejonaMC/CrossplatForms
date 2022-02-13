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
    private String name;

    @Required
    private CommandType method;

    @Nullable
    private BasicClickAction action = null;

    @Nullable
    private BasicClickAction bedrockAction = null;

    @Nullable
    private BasicClickAction javaAction = null;

    @Setter
    private String permission;

    public void run(Player player, InterfaceManager interfaceManager, BedrockHandler bedrockHandler) {
        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player.getHandle();
        if (action != null) {
            action.affectPlayer(bukkitPlayer, interfaceManager, bedrockHandler);
        }
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (bedrockAction != null) {
                bedrockAction.affectPlayer(bukkitPlayer, interfaceManager, bedrockHandler);
            }
        } else {
            if (javaAction != null) {
                javaAction.affectPlayer(bukkitPlayer, interfaceManager, bedrockHandler);
            }
        }
    }
}
