package dev.projectg.crossplatforms.spigot.common;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.MenuAction;
import dev.projectg.crossplatforms.serialize.SimpleType;
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
        Player spigotPlayer = (Player) player;
        spigotPlayer.closeInventory();
    }
}
