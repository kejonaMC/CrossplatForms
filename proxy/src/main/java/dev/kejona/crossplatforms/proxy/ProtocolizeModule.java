package dev.kejona.crossplatforms.proxy;

import com.google.inject.AbstractModule;
import dev.kejona.crossplatforms.item.InventoryController;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.proxy.item.ProtocolizeInventoryController;
import dev.kejona.crossplatforms.proxy.item.ProtocolizeInventoryFactory;

public class ProtocolizeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(InventoryFactory.class).to(ProtocolizeInventoryFactory.class);
        bind(InventoryController.class).to(ProtocolizeInventoryController.class);
    }
}
