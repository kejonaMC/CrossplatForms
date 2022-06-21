package dev.kejona.crossplatforms.spigot.common;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.interfacing.java.MenuAction;
import dev.kejona.crossplatforms.serialize.SimpleType;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CloseMenuAction extends SimpleType<String> implements MenuAction {

    public static final String TYPE = "close";

    @Inject
    public CloseMenuAction() {
        super(TYPE, "");
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull JavaMenu menu) {
        Player spigotPlayer = (Player) player.getHandle();
        spigotPlayer.closeInventory();
    }
}
