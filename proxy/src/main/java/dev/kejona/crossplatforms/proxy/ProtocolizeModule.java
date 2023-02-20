package dev.kejona.crossplatforms.proxy;

import com.google.inject.AbstractModule;
import dev.kejona.crossplatforms.inventory.InventoryController;
import dev.kejona.crossplatforms.inventory.InventoryFactory;
import dev.kejona.crossplatforms.proxy.inventory.ProtocolizeInventoryController;
import dev.kejona.crossplatforms.proxy.inventory.ProtocolizeInventoryFactory;

public class ProtocolizeModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(InventoryFactory.class).to(ProtocolizeInventoryFactory.class);
        bind(InventoryController.class).to(ProtocolizeInventoryController.class);
    }
}
