package dev.kejona.crossplatforms.interfacing;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenu;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.resolver.Resolver;

public class NoMenusInterfacer extends Interfacer {

    @Override
    public void openInventory(FormPlayer recipient, JavaMenu menu, Inventory inventory, Resolver resolver) {
        throw new UnsupportedOperationException("Inventory menus are not supported.");
    }
}
