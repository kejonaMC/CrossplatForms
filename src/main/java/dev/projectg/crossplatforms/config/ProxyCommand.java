package dev.projectg.crossplatforms.config;

import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.Player;
import dev.projectg.crossplatforms.interfacing.BasicClickAction;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.permission.Permission;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ProxyCommand {

    @NodeKey
    private String name;

    private CommandType method;

    private BasicClickAction all = null;
    private BasicClickAction bedrock = null;
    private BasicClickAction java = null;

    private Permission permission;

    public void run(Player player, InterfaceManager interfaceManager, BedrockHandler bedrockHandler) {
        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player;
        if (all != null) {
            all.affectPlayer(bukkitPlayer, interfaceManager, bedrockHandler);
        }
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (bedrock != null) {
                bedrock.affectPlayer(bukkitPlayer, interfaceManager, bedrockHandler);
            }
        } else {
            if (java != null) {
                java.affectPlayer(bukkitPlayer, interfaceManager, bedrockHandler);
            }
        }
    }
}
