package dev.kejona.crossplatforms.proxy;

import dev.kejona.crossplatforms.action.ActionSerializer;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.interfacing.java.MenuAction;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;

import javax.annotation.Nonnull;

public class CloseMenuAction implements MenuAction {

    private static final String TYPE = "close";
    private static final ProtocolizePlayerProvider PLAYER_PROVIDER = Protocolize.playerProvider();

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull JavaMenu menu) {
        PLAYER_PROVIDER.player(player.getUuid()).closeInventory();
    }

    @Override
    public String type() {
        return TYPE;
    }

    public static void register(ActionSerializer serializer) {
        serializer.menuAction(TYPE, CloseMenuAction.class);
    }
}
