package dev.projectg.crossplatforms.interfacing;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;

public class NoMenusInterfacer extends InterfaceManager {

    @Override
    public void sendMenu(FormPlayer player, JavaMenu menu) {
        throw new UnsupportedOperationException("Inventory menus are not supported.");
    }
}
