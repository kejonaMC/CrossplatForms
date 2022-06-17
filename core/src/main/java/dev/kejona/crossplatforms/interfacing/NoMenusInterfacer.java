package dev.kejona.crossplatforms.interfacing;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;

public class NoMenusInterfacer extends Interfacer {

    @Override
    public void sendMenu(FormPlayer player, JavaMenu menu) {
        throw new UnsupportedOperationException("Inventory menus are not supported.");
    }
}
