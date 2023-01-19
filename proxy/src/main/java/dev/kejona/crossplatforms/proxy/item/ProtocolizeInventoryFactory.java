package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.item.Item;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.UUID;

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
        item.displayName(displayName);
        item.lore(lore, true);

        CompoundTag nbt = item.nbtData();
        if (customModelData.isPresent()) {
            nbt.putInt(CUSTOM_MODEL_DATA_KEY, customModelData.getAsInt());
        } else {
            nbt.remove(CUSTOM_MODEL_DATA_KEY);
        }

        return new ProtocolizeItem(item);
    }

    @Override
    public Item skullItem(FormPlayer owner, @Nullable String displayName, List<String> lore) {
        ItemStack item = new ItemStack(ItemType.PLAYER_HEAD);
        if (displayName != null) {
            item.displayName(displayName);
        }
        item.lore(lore, true);

        // https://minecraft.fandom.com/wiki/Head#Item_data
        CompoundTag skullOwner = new CompoundTag();
        skullOwner.putIntArray("Id", uuidToArray(owner.getUuid())); // todo: different for below 1.16 apparently
        skullOwner.putString("Name", owner.getName());
        skullOwner.put("Properties", propertiesForTextures(owner.getEncodedSkinData()));

        item.nbtData().put("SkullOwner", skullOwner);

        return new ProtocolizeItem(item);
    }

    private CompoundTag propertiesForTextures(String encodedTexture) {
        CompoundTag properties = new CompoundTag();
        ListTag<CompoundTag> textures = new ListTag<>(CompoundTag.class);
        CompoundTag texture = new CompoundTag();

        properties.put("textures", textures);
        textures.add(texture);
        texture.putString("Value", encodedTexture);

        return properties;
    }

    private static int[] uuidToArray(UUID uuid) {
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();

        return new int[] {
            (int) (least >> 32),
            (int) least,
            (int) (most >> 32),
            (int) most
        };
    }

    private static InventoryType convertType(InventoryLayout layout) {
        switch (layout) {
            case HOPPER:
                return InventoryType.HOPPER;
            case DISPENSER:
                return InventoryType.GENERIC_3X3;
            case CHEST:
                throw new IllegalArgumentException("Chest inventories should be created directly");
        }

        throw new AssertionError("Unhandled InventoryLayout: " + layout.name());
    }
}
