package dev.projectg.crossplatforms.proxy;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.interfacing.java.MenuAction;
import dev.projectg.crossplatforms.serialize.SimpleType;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;

import javax.annotation.Nonnull;

public class CloseMenuAction extends SimpleType<Boolean> implements MenuAction {

    public static final String TYPE = "close";
    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();

    public CloseMenuAction(@Nonnull Boolean value) {
        super(TYPE, value);
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull JavaMenu menu, @Nonnull InterfaceManager interfaceManager) {
        PLAYER_PROVIDER.player(player.getUuid()).closeInventory();
    }
}
