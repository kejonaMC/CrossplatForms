package dev.kejona.crossplatforms.proxy.inventory;

import dev.kejona.crossplatforms.inventory.InventoryHandle;
import dev.kejona.crossplatforms.inventory.ItemHandle;
import dev.simplix.protocolize.api.inventory.Inventory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProtocolizeInventory implements InventoryHandle {

    private final Inventory inventory;

    @Override
    public Object handle() {
        return inventory;
    }

    @Override
    public String title() {
        return inventory.title(true);
    }

    @Override
    public void setSlot(int index, ItemHandle item) {
        inventory.item(index, item.castedHandle());
    }
}
