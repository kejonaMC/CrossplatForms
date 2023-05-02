package dev.kejona.crossplatforms.spigot.item;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.inventory.InventoryFactory;
import dev.kejona.crossplatforms.inventory.InventoryHandle;
import dev.kejona.crossplatforms.inventory.InventoryLayout;
import dev.kejona.crossplatforms.inventory.ItemHandle;
import dev.kejona.crossplatforms.inventory.SkullProfile;
import dev.kejona.crossplatforms.spigot.adapter.SpigotAdapter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SpigotInventoryFactory implements InventoryFactory {

    private static final int MAX_CHEST_SIZE = 9 * 5;

    private static boolean warnedForCustomModelData = false;

    private final Logger logger = Logger.get();
    private final SpigotAdapter adapter;
    private final Material playerHeadMaterial;

    public SpigotInventoryFactory(SpigotAdapter adapter) {
        this.adapter = adapter;
        this.playerHeadMaterial = adapter.playerHeadMaterial();
    }

    @Override
    public InventoryHandle chest(String title, int chestSize) {
        Inventory inventory = Bukkit.createInventory(null, chestSize, title);
        return new SpigotInventory(inventory);
    }

    @Override
    public InventoryHandle inventory(String title, InventoryLayout layout) {
        if (layout == InventoryLayout.CHEST) {
            return chest(title, MAX_CHEST_SIZE);
        }

        Inventory inventory = Bukkit.createInventory(null, convertType(layout), title);
        return new SpigotInventory(inventory);
    }

    @Override
    public ItemHandle item(@Nullable String material, @Nullable String displayName, @Nonnull List<String> lore, @Nullable Integer customModelData) {

        Material type;
        if (material == null || material.isEmpty()) {
            type = Material.STONE;
        } else {
            type = Material.matchMaterial(material);
            if (type == null) {
                Logger.get().warn("Material '" + material + "' is not a valid material. Check the Material enum for this specific server version.");
                type = Material.STONE;
            }
        }

        ItemStack item = new ItemStack(type);

        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
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
    public ItemHandle skullItem(FormPlayer profile, @Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        SkullMeta meta = skullMeta(item);
        adapter.setSkullProfile(meta, profile);
        item.setItemMeta(meta);
        return new SpigotItem(item);
    }

    @Override
    public ItemHandle skullItem(SkullProfile profile, @Nullable String displayName, List<String> lore) {
        ItemStack item = skullBase(displayName, lore);
        SkullMeta meta = skullMeta(item);
        adapter.setSkullProfile(meta, profile.getOwner(), profile.getTextures());
        item.setItemMeta(meta);
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
        return (SkullMeta) skullItem.getItemMeta();
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
    private static class SpigotItem implements ItemHandle {

        private final ItemStack handle;

        @Override
        public Object handle() {
            return handle;
        }
    }

    @RequiredArgsConstructor
    private static class SpigotInventory implements InventoryHandle {

        private final Inventory handle;

        @Override
        public Object handle() {
            return handle;
        }

        @Override
        public String title() {
            return handle.getTitle();
        }

        @Override
        public void setSlot(int index, ItemHandle item) {
            handle.setItem(index, item.castedHandle(ItemStack.class));
        }
    }
}
