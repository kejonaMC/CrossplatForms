package dev.projectg.geyserhub.form;

import dev.projectg.geyserhub.CrossplatForms;
import dev.projectg.geyserhub.utils.PlaceholderUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class AccessItem {

    /**
     * A key that should be of type {@link #ACCESS_ITEM_KEY_TYPE}, with the value being the {@link #itemId}
     */
    public static final NamespacedKey ACCESS_ITEM_KEY = new NamespacedKey(CrossplatForms.getInstance(), "geyserHubAccessItem");
    public static final PersistentDataType<String, String> ACCESS_ITEM_KEY_TYPE = PersistentDataType.STRING;

    /**
     * The id of the access item.
     */
    public final String itemId;

    /**
     * The hotbar slot for the item to be, by default.
     * Should be placed elsewhere in the hotbar if not possible.
     * Should not be given if the hotbar is full.
     */
    public final int slot;

    private final String displayName;
    private final Material material;
    private final List<String> lore;

    /**
     * True for players to receive it when they join
     */
    public final boolean onJoin;

    /**
     * True for players to receive it when they respawn
     */
    public final boolean onRespawn;

    /**
     * True for players to be able to drop the item
     */
    public final boolean allowDrop;

    /**
     * True for the item to be destroyed when dropped
     */
    public final boolean destroyDropped;

    /**
     * True for players to be able to move the item
     */
    public final boolean allowMove;

    /**
     * The form/menu name to open.
     */
    public final String formName;

    /**
     * Immutable Access Item to open a form.
     * @param itemId The ID of the form (the parent key in the config, generally)
     * @param slot The slot of the inventory this should be put in by default
     * @param displayName The Display Name of the ItemStack, supports placeholders.
     * @param material The material for the ItemStack to be
     * @param lore The lore for the ItemStack
     * @param onJoin True for players to receive it when they join
     * @param onRespawn True for players to receive it when they respawn (excluding server join)
     * @param allowDrop True for players to be able to drop the item
     * @param destroyDropped True for the item to be destroyed when dropped
     * @param allowMove True for players to be able to move the item
     * @param formName The form/menu name to open.
     */
    public AccessItem(@Nonnull String itemId, int slot, @Nonnull String displayName, @Nonnull Material material, @Nonnull List<String> lore,
                      boolean onJoin, boolean onRespawn, boolean allowDrop, boolean destroyDropped, boolean allowMove,
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
        this.onRespawn = onRespawn;
        this.allowDrop = allowDrop;
        this.destroyDropped = destroyDropped;
        this.allowMove = allowMove;

        this.formName = formName;
    }

    private ItemStack createItemStack(@Nonnull String displayName, @Nonnull Material material, @Nonnull List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(ACCESS_ITEM_KEY, ACCESS_ITEM_KEY_TYPE, itemId);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * @return The ItemStack of the access item, with no placeholders set.
     */
    public ItemStack getItemStack() {
        return createItemStack(displayName, material, lore);
    }

    /**
     * @param player The player to apply to placeholders
     * @return The ItemStack of the access item, with placeholders in the display name and lore set according to the given player
     */
    public ItemStack getItemStack(@Nonnull Player player) {
        String displayName = PlaceholderUtils.setPlaceholders(player, this.displayName);
        List<String> lore = PlaceholderUtils.setPlaceholders(player, this.lore);
        return createItemStack(displayName, material, lore);
    }
}
