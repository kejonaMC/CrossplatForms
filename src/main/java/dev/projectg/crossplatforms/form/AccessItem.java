package dev.projectg.crossplatforms.form;

import com.google.common.collect.ImmutableMap;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.Platform;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private static final String permissionBase = "crossplatforms.item.";

    @NodeKey
    @Required
    private String identifier = null;

    @Required
    private String form = null;

    @Required
    private String material = null;

    @Required
    private String displayName = null;
    private List<String> lore = Collections.emptyList();
    private int slot = 0;

    private boolean onJoin = false;
    private boolean onRespawn = false;
    private boolean onWorldChange = false;

    private Platform platform = Platform.ALL;
    private Map<Limit, PermissionDefault> permissionDefaults = Collections.emptyMap();

    private boolean persist = false;

    // Stuff that is generated after deserialization, once the identifier has been loaded
    private transient Map<Limit, Permission> permissions;

    @Getter(AccessLevel.NONE)
    private transient String mainPermission;

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
     * @param player The player to apply to placeholders
     * @return The ItemStack of the access item, with placeholders in the display name and lore set according to the given player
     */
    public ItemStack createItemStack(@Nonnull Player player) {
        String displayName = PlaceholderUtils.setPlaceholders(player, this.displayName);
        List<String> lore = PlaceholderUtils.setPlaceholders(player, this.lore);
        return createItemStack(identifier, displayName, material, lore);
    }

    public void generatePermissions(AccessItemRegistry registry) {
        if (permissions != null) {
            Logger.getLogger().severe("Permissions in Access Item " + identifier + " have already been generated!");
            Thread.dumpStack();
        }

        mainPermission = permissionBase + identifier;

        ImmutableMap.Builder<Limit, Permission> builder = ImmutableMap.builder();
        for (Limit limit : Limit.values()) {
            // Alright this is a bit janky. 1st, attempt to retrieve the permission default from this specific config.
            // If it is not specified for this item, then we check the global permission defaults.
            // If the user has not specified anything in the globals, then we use fallback values
            PermissionDefault permissionDefault = permissionDefaults.getOrDefault(limit, registry.getGlobalPermissionDefaults().getOrDefault(limit, limit.fallbackDefault));
            builder.put(limit, new Permission(mainPermission + limit.permissionSuffix, limit.description, permissionDefault));
        }

        permissions = builder.build();
    }

    public String getPermission(Limit limit) {
        return permissions.get(limit).key();
    }

    /**
     * Permissions that limit the usage of the Access Item.
     */
    @RequiredArgsConstructor
    public enum Limit {
        POSSESS(".possess", "Possess the Access Item", PermissionDefault.TRUE),
        EVENT(".event", "Get the Access Item from its defined events", PermissionDefault.TRUE),
        COMMAND(".command", "Ability to get the Access Item through /forms give <access item>", PermissionDefault.OP),
        DROP(".drop", "Ability to remove the Access Item from your inventory", PermissionDefault.FALSE),
        PRESERVE(".preserve", "Stop the Access Item from being destroyed when it is dropped", PermissionDefault.FALSE),
        MOVE(".move", "Ability to move the Access Item around and to inventories", PermissionDefault.FALSE);

        /**
         * Map of {@link PermissionDefault} to fallback to if the user does not define their own.
         */
        public static final Map<Limit, PermissionDefault> FALLBACK_DEFAULTS;

        static {
            ImmutableMap.Builder<Limit, PermissionDefault> builder = ImmutableMap.builder();
            for (Limit limit : Limit.values()) {
                builder.put(limit, limit.fallbackDefault);
            }
            FALLBACK_DEFAULTS = builder.build();
        }

        public final String permissionSuffix;
        public final String description;
        public final PermissionDefault fallbackDefault;
    }
}
