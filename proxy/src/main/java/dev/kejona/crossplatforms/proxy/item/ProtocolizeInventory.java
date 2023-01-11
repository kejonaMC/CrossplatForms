package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.item.Item;
import dev.simplix.protocolize.api.inventory.Inventory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProtocolizeInventory implements dev.kejona.crossplatforms.item.Inventory {

    private final Inventory inventory;

    @Override
    public Object handle() {
        return inventory;
    }

    @Override
    public void setSlot(int index, Item item) {
        inventory.item(index, item.castedHandle());
    }
}
