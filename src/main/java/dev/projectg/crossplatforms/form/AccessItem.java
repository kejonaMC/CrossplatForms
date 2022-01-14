package dev.projectg.crossplatforms.form;

import com.google.common.collect.ImmutableSet;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.Platform;
import dev.projectg.crossplatforms.permission.DefaultPermission;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class AccessItem {

    /**
     * A key that should be of type {@link #ACCESS_ITEM_KEY_TYPE}, with the value being the {@link #identifier} of the Access Item.
     * Used for identifying items that are Access Items.
     */
    public static final NamespacedKey ACCESS_ITEM_KEY = new NamespacedKey(CrossplatForms.getInstance(), "crossplatFormsAccessItem");
    public static final PersistentDataType<String, String> ACCESS_ITEM_KEY_TYPE = PersistentDataType.STRING;

    private static final String permissionBase = "crossplatforms.accessitem.";

    /**
     * The ID of the Access Item.
     */
    @NodeKey
    @Required
    private String identifier = "";

    private transient String mainPermission = permissionBase + identifier;

    /**
     * The material of the item. Currently must be a string representation of Bukkit's Material enum.
     */
    @Required
    private String material = "";

    /**
     * The display name of the item that the user sees
     */
    @Required
    private String displayName = "";

    /**
     * Lore to be applied to the item stack.
     */
    private List<String> lore = Collections.emptyList();

    /**
     * The inventory slot (ideally hotbar) for the item to be in, by default.
     * Should be placed elsewhere in the hotbar if not possible.
     * Should not be given if the hotbar is full.
     */
    @Required
    private int slot = 0;

    /**
     * The form or menu to open
     */
    @Required
    private String form = "";

    /**
     * Which platform(s) the Access Item should be given to.
     */
    private Platform platform = Platform.ALL;
    // todo: example

    /**
     * If the access item should be given/removed on join
     */
    private boolean onJoin = false;

    /**
     * If the access items should be given/removed on respawn
     */
    private boolean onRespawn = false;

    /**
     * If the access item should be given/removed on world change
     */
    private boolean onWorldChange = false;
    // todo: example

    /**
     * True if the player can retrieve the access item through commands
     */
    @Nullable
    private DefaultPermission defaultPermission = null;

    /**
     * If the player should be allowed to drop the item.
     */
    private boolean allowDrop = false;
    private transient String dropPermission = mainPermission + ".drop";

    /**
     * If the item should be destroyed when it is dropped.
     */
    private boolean destroyDropped = true;
    private transient String noDestroyPermission = mainPermission + ".nodestroy";

    /**
     * If the access item should be allowed to be moved within the inventory, and to other inventories.
     */
    private boolean allowMove = false;
    private transient String movePermission = mainPermission + ".move";

    private transient Set<Permission> permissions;

    /**
     * If the access item should be persisted on player leave (stay in the players inventory)
     */
    private boolean persist = false;
    // todo: example

    private static ItemStack createItemStack(@Nonnull String identifier, @Nonnull String displayName, @Nonnull String materialId, @Nonnull List<String> lore) {
        Material material = Material.getMaterial(materialId);
        if (material == null) {
            Logger.getLogger().severe("Failed to find material from string '" + materialId + "' in Access Item: " + identifier);
            material = Material.STONE;
            // todo: do this better somewhere else
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(ACCESS_ITEM_KEY, ACCESS_ITEM_KEY_TYPE, identifier);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * @return The ItemStack of the access item, with no placeholders set.
     */
    public ItemStack createItemStack() {
        return createItemStack(identifier, displayName, material, lore);
    }

    /**
     * @param player The player to apply to placeholders
     * @return The ItemStack of the access item, with placeholders in the display name and lore set according to the given player
     */
    public ItemStack createItemStack(@Nonnull Player player) {
        String displayName = PlaceholderUtils.setPlaceholders(player, this.displayName);
        List<String> lore = PlaceholderUtils.setPlaceholders(player, this.lore);
        return createItemStack(identifier, displayName, material, lore);
    }

    public void generatePermissions(AccessItemRegistry registry) {
        permissions = ImmutableSet.of(
                new Permission(
                        mainPermission,
                        "Ability to retrieve the access item through commands",
                        getEffectiveDefaultPermission(registry)),
                new Permission(
                        dropPermission,
                        "Permission to drop the access item",
                        DefaultPermission.from(allowDrop)),
                new Permission(
                        noDestroyPermission,
                        "Permission to prevent the access item from being destroyed when dropped",
                        DefaultPermission.from(!destroyDropped)),
                new Permission(
                        movePermission,
                        "Permission to move the access item within the player inventory and to different inventories.",
                        DefaultPermission.from(allowMove))
        );
    }

    public DefaultPermission getEffectiveDefaultPermission(AccessItemRegistry registry) {
        if (defaultPermission == null) {
            return registry.getGlobalDefaultPermission();
        } else {
            return defaultPermission;
        }
    }
}
