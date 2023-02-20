package dev.kejona.crossplatforms.proxy.inventory;

import dev.kejona.crossplatforms.inventory.ItemHandle;
import dev.simplix.protocolize.api.item.ItemStack;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProtocolizeItem implements ItemHandle {

    private final ItemStack item;

    @Override
    public Object handle() {
        return item;
    }
}
