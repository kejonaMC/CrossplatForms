package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.item.Item;
import dev.simplix.protocolize.api.item.BaseItemStack;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProtocolizeItem implements Item {


    private final BaseItemStack item;

    @Override
    public Object handle() {
        return item;
    }
}
