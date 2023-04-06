package dev.kejona.crossplatforms.accessitem;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Constants;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.Platform;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.inventory.ConfiguredItem;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.permission.PermissionDefault;
import dev.kejona.crossplatforms.resolver.Resolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class AccessItem extends ConfiguredItem {

    public static final String STATIC_IDENTIFIER = "crossplatformsaccessitem"; // changing this will break existing setups
    private static final String PERMISSION_BASE = Constants.Id() + ".item";

    @Inject
    private transient BedrockHandler bedrockHandler;

    @Inject
    private transient Placeholders placeholders;

    /**
     * The reliable identifier of the access item
     */
    @NodeKey
    @Required
    private String identifier = null;

    private List<Action<? super AccessItem>> actions = Collections.emptyList();
    private List<Action<? super AccessItem>> bedrockActions = Collections.emptyList();
    private List<Action<? super AccessItem>> javaActions = Collections.emptyList();

    /**
     * The inventory slot to be placed in
     */
    private int slot = 0;


    /**
     * If there should be an attempt to give access items on join events.
     */
    private boolean onJoin = false;

    /**
     * If there should be an attempt to give access items on respawn events.
     */
    private boolean onRespawn = false;

    /**
     * If there should be an attempt to give access items on world change events,
     */
    private boolean onWorldChange = false;

    private Platform platform = Platform.ALL;
    private Map<Limit, PermissionDefault> permissionDefaults = Collections.emptyMap();

    /**
     * If persist is disable, access items will be removed from player inventories on server leave
     */
    private boolean persist = false;

    // Stuff that is generated after deserialization, once the identifier has been loaded
    private transient Map<Limit, Permission> permissions;

    public void trigger(FormPlayer player) {
        Resolver resolver = placeholders.resolver(player);

        Action.affectPlayer(player, actions, resolver, this);
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            Action.affectPlayer(player, bedrockActions, resolver, this);
        } else {
            Action.affectPlayer(player, javaActions, resolver, this);
        }
    }

    public void generatePermissions(AccessItemRegistry registry) {
        if (permissions != null) {
            Logger.get().warn("Permissions in Access Item '" + identifier + "' have already been generated!");
        }

        String mainPermission = PERMISSION_BASE + "." + identifier;

        permissions = new HashMap<>();
        for (Limit limit : Limit.values()) {
            // Alright this is a bit janky. 1st, attempt to retrieve the permission default from this specific config.
            // If it is not specified for this item, then we check the global permission defaults.
            // If the user has not specified anything in the globals, then we use fallback values
            PermissionDefault permissionDefault = permissionDefaults.getOrDefault(limit, registry.getGlobalPermissionDefaults().getOrDefault(limit, limit.fallbackDefault));
            permissions.put(limit, new Permission(mainPermission + limit.permissionSuffix, limit.description, permissionDefault));
        }
    }

    public String permission(Limit limit) {
        return permissions.get(limit).key();
    }

    /**
     * Permissions that limit the usage of the Access Item.
     */
    @RequiredArgsConstructor
    public enum Limit {
        // Permissions regarding getting the Access Item
        POSSESS(".possess", "Possess the Access Item", PermissionDefault.TRUE),
        EVENT(".event", "Get the Access Item from its defined events", PermissionDefault.TRUE),
        COMMAND(".command", "Ability to get the Access Item through /forms give <access item>", PermissionDefault.OP),

        // Permissions regarding having the access item in the inventory
        DROP(".drop", "Ability to remove the Access Item from your inventory", PermissionDefault.FALSE),
        PRESERVE(".preserve", "Stop the Access Item from being destroyed when it is dropped. This includes death, regardless of drop permission.", PermissionDefault.FALSE),
        MOVE(".move", "Ability to move the Access Item around and to inventories", PermissionDefault.FALSE);

        public final String permissionSuffix;
        public final String description;
        public final PermissionDefault fallbackDefault;
    }
}
