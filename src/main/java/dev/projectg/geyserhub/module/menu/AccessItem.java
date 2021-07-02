package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.utils.PlaceholderUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class AccessItem {

    public final String itemId;
    public final int slot;
    private final String displayName;
    private final Material material;
    private final List<String> lore;

    public final boolean onJoin;
    public final boolean allowDrop;
    public final boolean destroyDropped;
    public final boolean allowMove;

    public AccessItem(@Nonnull String itemId, int slot, @Nonnull String displayName, @Nonnull Material material, @Nonnull List<String> lore,
                      boolean onJoin, boolean allowDrop, boolean destroyDropped, boolean allowMove,
                      @Nonnull String formName) {
        Objects.requireNonNull(material);
        if (new ItemStack(material).getItemMeta() == null) {
            throw new IllegalArgumentException("Cannot create an access item with a Material that results in an ItemStack with null ItemMeta!");
        }
        this.itemId = Objects.requireNonNull(itemId);
        this.slot = slot;
        this.displayName = Objects.requireNonNull(displayName);
        this.material = material;
        this.lore = Objects.requireNonNull(lore);

        this.onJoin = onJoin;
        this.allowDrop = allowDrop;
        this.destroyDropped = destroyDropped;
        this.allowMove = allowMove;
    }


    private ItemStack createItemStack(@Nonnull String displayName, @Nonnull Material material, @Nonnull List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.getPersistentDataContainer().get(new NamespacedKey("geyserHubAccessItem", ))
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getItemStack() {
        return createItemStack(displayName, material, lore);
    }

    public ItemStack getItemStack(@Nonnull Player player) {
        String displayName = PlaceholderUtils.setPlaceholders(player, this.displayName);
        List<String> lore = PlaceholderUtils.setPlaceholders(player, this.lore);
        return createItemStack(displayName, material, lore);
    }
}
