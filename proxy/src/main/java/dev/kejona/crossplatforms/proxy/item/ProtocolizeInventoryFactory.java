package dev.kejona.crossplatforms.proxy.item;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.item.Item;
import dev.kejona.crossplatforms.item.SkullProfile;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.api.providers.ProtocolizePlayerProvider;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProtocolizeInventoryFactory implements InventoryFactory {

    private static final int PROTOCOL_1_16 = 735;
    private static final String CUSTOM_MODEL_DATA_KEY = "CustomModelData";

    private final ProtocolizePlayerProvider players = Protocolize.playerProvider();

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
    public Item item(@Nonnull String displayName, @Nullable String material, @Nonnull List<String> lore, @Nullable Integer customModelData) {
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
        item.displayName(displayName);
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
    public Item skullItem(FormPlayer viewer, FormPlayer owner, @Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        setSkullOwner(viewer, item.nbtData(), owner.getUuid(), owner.getName(), owner.getEncodedSkinData());
        return new ProtocolizeItem(item);
    }

    @Override
    public Item skullItem(FormPlayer viewer, SkullProfile owner, @org.jetbrains.annotations.Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        setSkullOwner(viewer,
            item.nbtData(),
            owner.getOwnerId(),
            owner.getOwnerName(),
            owner.getTexturesValue()
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

    private void setSkullOwner(FormPlayer itemReceiver, CompoundTag tag, UUID id, String name, String textures) {
        if (id == null && name == null && textures == null) {
            throw new IllegalArgumentException("id, name, textures cannot all be null");
        }

        if (id == null && textures == null) {
            // old cruddy format of only the name
            tag.putString("SkullOwner", name);
            return;
        }

        if (id == null && name == null) {
            // only textures provided - use random UUID
            id = UUID.randomUUID();
        }

        CompoundTag skullOwner = new CompoundTag();
        if (id != null) {
            setSkullId(itemReceiver, skullOwner, id);
        }
        if (name != null) {
            skullOwner.putString("Name", name);
        }
        if (textures != null) {
            skullOwner.put("Properties", propertiesWithTextures(textures));
        }

        tag.put("SkullOwner", skullOwner);
    }

    private void setSkullId(FormPlayer itemReceiver, CompoundTag skullOwner, UUID id) {
        ProtocolizePlayer player = players.player(itemReceiver.getUuid());

        // UUID storage was changed in 1.16
        if (player.protocolVersion() < PROTOCOL_1_16) {
            skullOwner.putString("Id", id.toString());
        } else {
            skullOwner.putIntArray("Id", uuidToArray(id));
        }
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

    /**
     * Returns the format for storing UUIDs in NBT, for 1.16 and later
     */
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
