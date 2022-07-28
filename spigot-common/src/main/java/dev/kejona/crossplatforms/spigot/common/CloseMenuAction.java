package dev.kejona.crossplatforms.spigot.common;

import dev.kejona.crossplatforms.action.ActionSerializer;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.interfacing.java.MenuAction;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ConfigSerializable
public class CloseMenuAction implements MenuAction {

    private static final String TYPE = "close";

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull JavaMenu menu) {
        Player spigotPlayer = player.getHandle(Player.class);
        spigotPlayer.closeInventory();
    }

    @Override
    public String type() {
        return TYPE;
    }

    public static void register(ActionSerializer serializer) {
        serializer.menuAction(TYPE, CloseMenuAction.class);
    }
}
