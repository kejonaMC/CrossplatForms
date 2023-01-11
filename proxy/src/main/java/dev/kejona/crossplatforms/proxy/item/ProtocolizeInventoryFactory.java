package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.item.Item;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;

import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;

public class ProtocolizeInventoryFactory implements InventoryFactory {

    private static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";

    @Override
    public Inventory chest(String title, int chestSize) {
        InventoryType type = InventoryType.chestInventoryWithSize(chestSize);
        dev.simplix.protocolize.api.inventory.Inventory inventory = new dev.simplix.protocolize.api.inventory.Inventory(type);

        inventory.title(title);

        return new ProtocolizeInventory(inventory);
    }

    @Override
    public Inventory inventory(String title, InventoryLayout layout) {
        InventoryType type = convertType(layout);
        dev.simplix.protocolize.api.inventory.Inventory inventory = new dev.simplix.protocolize.api.inventory.Inventory(type);

        inventory.title(title);

        return new ProtocolizeInventory(inventory);
    }

    @Override
    public Item item(String displayName, String material, List<String> lore, OptionalInt customModelData) {

        ItemStack item = new ItemStack(ItemType.valueOf(material.toUpperCase(Locale.ROOT).trim()));

        CompoundTag nbt = item.nbtData();

        if (customModelData.isPresent()) {
            nbt.putInt(CUSTOM_MODEL_DATA_KEY, customModelData.getAsInt());
        } else {
            nbt.remove(CUSTOM_MODEL_DATA_KEY);
        }

        return new ProtocolizeItem(item);
    }

    @Override
    public Item skullItem(String owner, String displayName, List<String> lore) {
        return null;
    }

    @Override
    public Item skullItem(FormPlayer owner, String displayName, List<String> lore) {
        return null;
    }

    private static InventoryType convertType(InventoryLayout layout) {
        switch (layout) {
            case CHEST:
                return InventoryType.GENERIC_9X6;
            case HOPPER:
                return InventoryType.HOPPER;
            case DISPENSER:
                return InventoryType.GENERIC_3X3;
        }

        throw new AssertionError("Unhandled InventoryLayout: " + layout.name());
    }
}
