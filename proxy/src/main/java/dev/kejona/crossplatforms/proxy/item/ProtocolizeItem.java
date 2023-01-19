package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.item.Item;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProtocolizeItem implements Item {

    private final ItemStack item;

    @Override
    public Object handle() {
        return item;
    }
}
