package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.item.Item;

import java.util.List;
import java.util.OptionalInt;

public class ProtocolizeInventoryFactory implements InventoryFactory {
    @Override
    public Inventory chest(String title, int chestSize) {
        return null;
    }

    @Override
    public Inventory inventory(String title, InventoryLayout layout) {
        return null;
    }

    @Override
    public Item item(String displayName, String material, List<String> lore, OptionalInt customModelData) {
        return null;
    }

    @Override
    public Item skullItem(String owner, String displayName, List<String> lore) {
        return null;
    }

    @Override
    public Item skullItem(FormPlayer owner, String displayName, List<String> lore) {
        return null;
    }
}
