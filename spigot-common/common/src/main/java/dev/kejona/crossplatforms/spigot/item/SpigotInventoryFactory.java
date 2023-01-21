package dev.kejona.crossplatforms.spigot.item;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.item.Item;
import dev.kejona.crossplatforms.item.SkullProfile;
import dev.kejona.crossplatforms.spigot.adapter.VersionAdapter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class SpigotInventoryFactory implements InventoryFactory {

    private static final int MAX_CHEST_SIZE = 9 * 5;

    private static boolean warnedForCustomModelData = false;

    private final VersionAdapter adapter;
    private final Material playerHeadMaterial;
    private final Logger logger;

    public SpigotInventoryFactory(VersionAdapter adapter, Logger logger) {
        this.adapter = adapter;
        this.playerHeadMaterial = adapter.playerHeadMaterial();
        this.logger = logger;
    }

    @Override
    public Inventory chest(String title, int chestSize) {
        org.bukkit.inventory.Inventory inventory = Bukkit.createInventory(null, chestSize, title);
        return new SpigotInventory(inventory);
    }

    @Override
    public Inventory inventory(String title, InventoryLayout layout) {
        if (layout == InventoryLayout.CHEST) {
            return chest(title, MAX_CHEST_SIZE);
        }

        org.bukkit.inventory.Inventory inventory = Bukkit.createInventory(null, convertType(layout), title);
        return new SpigotInventory(inventory);
    }

    @Override
    public Item item(@Nonnull String displayName, @Nullable String material, @Nonnull List<String> lore, Integer customModelData) {
        Material type;
        if (material == null || material.isEmpty()) {
            type = Material.STONE;
        } else {
            try {
                type = Material.matchMaterial(material);
            } catch (IllegalArgumentException ignored) {
                Logger.get().warn("Material '" + material + "' is not a valid material on BungeeCord/Velocity (Protocolize)");
                type = Material.STONE;
            }
        }

        ItemStack item = new ItemStack(type);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);

        if (customModelData != null) {
            if (adapter.customModelData()) {
                // On a version that supports CustomModelData
                adapter.setCustomModelData(item, customModelData);
            } else {
                // Not supported, warn about it
                if (!warnedForCustomModelData) {
                    logger.warn("Cannot set CustomModelData of " + customModelData + " on item with name " + displayName);
                    logger.warn("Custom model data is not supported below 1.14");
                    warnedForCustomModelData = true;
                }
            }
        }

        return new SpigotItem(item);
    }

    @Override
    public Item skullItem(FormPlayer viewer, FormPlayer owner, @Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        adapter.setSkullProfile(skullMeta(item), owner);
        return new SpigotItem(item);
    }

    @Override
    public Item skullItem(FormPlayer viewer, SkullProfile owner, @Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        adapter.setSkullProfile(skullMeta(item), owner.getOwnerId(), owner.getOwnerName(), owner.getTexturesValue());
        return new SpigotItem(item);
    }

    private ItemStack skullBase(String displayName, List<String> lore) {
        ItemStack item = new ItemStack(playerHeadMaterial);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private SkullMeta skullMeta(ItemStack skullItem) {
        return (SkullMeta) skullItem;
    }

    private static InventoryType convertType(InventoryLayout layout) {
        switch (layout) {
            case HOPPER:
                return InventoryType.HOPPER;
            case DISPENSER:
                return InventoryType.DISPENSER;
            case CHEST:
                throw new IllegalArgumentException("Chest inventories should be created directly");
        }

        throw new AssertionError("Unhandled InventoryLayout: " + layout.name());
    }

    @RequiredArgsConstructor
    private static class SpigotItem implements Item {

        private final ItemStack handle;

        @Override
        public Object handle() {
            return handle;
        }
    }

    @RequiredArgsConstructor
    private static class SpigotInventory implements Inventory {

        private final org.bukkit.inventory.Inventory handle;

        @Override
        public Object handle() {
            return handle;
        }

        @Override
        public void setSlot(int index, Item item) {
            handle.setItem(index, item.castedHandle());
        }
    }
}
