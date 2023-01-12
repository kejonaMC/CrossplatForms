package dev.kejona.crossplatforms.spigot.common.item;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.item.Inventory;
import dev.kejona.crossplatforms.item.InventoryFactory;
import dev.kejona.crossplatforms.item.InventoryLayout;
import dev.kejona.crossplatforms.item.Item;
import dev.kejona.crossplatforms.spigot.common.adapter.VersionAdapter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public class SpigotInventoryFactory implements InventoryFactory {

    private static final Material PLAYER_HEAD_MATERIAL;

    static {
        Material modern = Material.getMaterial("PLAYER_HEAD");
        if (modern != null) {
            PLAYER_HEAD_MATERIAL = modern;
        } else {
            Logger.get().debug("Using legacy SKULL_ITEM material for player heads");
            PLAYER_HEAD_MATERIAL = Objects.requireNonNull(Material.getMaterial("SKULL_ITEM"), "SKULL_ITEM material lookup");
        }
    }

    private final Material playerHeadMaterial;

    public SpigotInventoryFactory(VersionAdapter adapter) {
        playerHeadMaterial = adapter.playerHeadMaterial();
    }

    @Override
    public Inventory chest(String title, int chestSize) {
        return new SpigotInventory(title, chestSize);
    }

    @Override
    public Inventory inventory(String title, InventoryLayout layout) {
        return new SpigotInventory(title, layout);
    }

    @Override
    public Item item(String displayName, String material, List<String> lore, OptionalInt customModelData) {
        ItemStack item = new ItemStack(Material.matchMaterial(material));

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return new SpigotItem(item);
    }

    @Override
    public Item skullItem(FormPlayer player, @Nullable String displayName, List<String> lore) {
        ItemStack item = new ItemStack(playerHeadMaterial);

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (displayName != null) {
            meta.setDisplayName(displayName);
        }
        meta.setLore(lore);


        item.setItemMeta(meta);
        return new SpigotItem(item);
    }
}
