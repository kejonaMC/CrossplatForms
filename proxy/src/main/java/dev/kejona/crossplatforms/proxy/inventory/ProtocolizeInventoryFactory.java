package dev.kejona.crossplatforms.proxy.inventory;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.inventory.InventoryFactory;
import dev.kejona.crossplatforms.inventory.InventoryHandle;
import dev.kejona.crossplatforms.inventory.InventoryLayout;
import dev.kejona.crossplatforms.inventory.ItemHandle;
import dev.kejona.crossplatforms.inventory.SkullProfile;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ProtocolizeInventoryFactory implements InventoryFactory {

    private static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";

    @Override
    public InventoryHandle chest(String title, int chestSize) {
        InventoryType type = InventoryType.chestInventoryWithSize(chestSize);
        Inventory inventory = new Inventory(type);

        inventory.title(title);

        return new ProtocolizeInventory(inventory);
    }

    @Override
    public InventoryHandle inventory(String title, InventoryLayout layout) {
        InventoryType type = convertType(layout);
        Inventory inventory = new Inventory(type);

        inventory.title(title);

        return new ProtocolizeInventory(inventory);
    }

    @Override
    public ItemHandle item(@Nullable String material, @Nullable String displayName, @Nonnull List<String> lore, @Nullable Integer customModelData) {
        ItemType type;
        if (material == null || material.isEmpty()) {
            type = ItemType.STONE;
        } else {
            try {
                type = ItemType.valueOf(material.toUpperCase(Locale.ROOT).trim());
            } catch (IllegalArgumentException ignored) {
                Logger.get().warn("Material '" + material + "' is not a valid material on BungeeCord/Velocity (Protocolize)");
                type = ItemType.STONE;
            }
        }

        ItemStack item = new ItemStack(type);
        if (displayName != null) {
            item.displayName(displayName);
        }
        item.lore(lore, true);

        CompoundTag nbt = item.nbtData();
        if (customModelData != null) {
            nbt.putInt(CUSTOM_MODEL_DATA_KEY, customModelData);
        } else {
            nbt.remove(CUSTOM_MODEL_DATA_KEY);
        }

        return new ProtocolizeItem(item);
    }

    @Override
    public ItemHandle skullItem(FormPlayer profile, @Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        setSkullOwner(item.nbtData(), profile.getName(), profile.getEncodedSkinData());
        return new ProtocolizeItem(item);
    }

    @Override
    public ItemHandle skullItem(SkullProfile profile, @Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        setSkullOwner(
            item.nbtData(),
            profile.getOwner(),
            profile.getTextures()
        );

        return new ProtocolizeItem(item);
    }

    private ItemStack skullBase(@Nullable String displayName, List<String> lore) {
        ItemStack item = new ItemStack(ItemType.PLAYER_HEAD);
        if (displayName != null) {
            item.displayName(displayName);
        }
        item.lore(lore, true);

        return item;
    }

    private void setSkullOwner(CompoundTag tag, String owner, String textures) {
        if (textures == null) {
            if (owner == null) {
                throw new IllegalArgumentException("both owner and textures cannot be null");
            }

            // old cruddy format of only the owner
            tag.putString("SkullOwner", owner);
            return;
        }

        String name;
        if (owner == null) {
            // silly fallback since providing UUID instead of name would mean
            // having to handle the two different UUID formats, which is quite complicated and ugly
            name = "spinbom";
        } else {
            name = owner;
        }

        CompoundTag skullOwner = new CompoundTag();
        skullOwner.putString("Name", name); // client doesn't seem to complain about not having a UUID
        skullOwner.put("Properties", propertiesWithTextures(textures));

        tag.put("SkullOwner", skullOwner);
    }

    private CompoundTag propertiesWithTextures(String encodedTexture) {
        CompoundTag texture = new CompoundTag();
        texture.putString("Value", encodedTexture);

        ListTag<CompoundTag> textures = new ListTag<>(CompoundTag.class);
        textures.add(texture);

        CompoundTag properties = new CompoundTag();
        properties.put("textures", textures);
        return properties;
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
