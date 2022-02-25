package dev.projectg.crossplatforms.accessitem;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.Platform;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.permission.PermissionDefault;
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
public class AccessItem {

    public static final String STATIC_IDENTIFIER = "crossplatFormsAccessItem"; // changing this will break existing setups
    private static final String PERMISSION_BASE = Constants.ID + ".item";

    /**
     * The reliable identifier of the access item
     */
    @NodeKey
    @Required
    private String identifier = null;

    private List<Action> actions = null;

    private List<Action> bedrockActions = null;

    private List<Action> javaActions = null;

    @Required
    private String material = null;

    /**
     * Display name of the itemstack
     */
    @Required
    private String displayName = null;

    /**
     * Itemstack lore
     */
    private List<String> lore = Collections.emptyList();

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

    public void trigger(FormPlayer player, InterfaceManager interfaceManager, BedrockHandler bedrockHandler) {
        if (actions != null) {
            for (Action action : actions) {
                action.affectPlayer(player, interfaceManager, bedrockHandler);
            }
        }
        if (bedrockHandler.isBedrockPlayer(player.getUuid())) {
            if (bedrockActions != null) {
                for (Action action : bedrockActions) {
                    action.affectPlayer(player, interfaceManager, bedrockHandler);
                }
            }
        } else {
            if (javaActions != null) {
                for (Action action : javaActions) {
                    action.affectPlayer(player, interfaceManager, BedrockHandler.empty());
                }
            }
        }
    }

    public void generatePermissions(AccessItemRegistry registry) {
        if (permissions != null) {
            Logger.getLogger().severe("Permissions in Access Item " + identifier + " have already been generated!");
            Thread.dumpStack();
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

        /**
         * Map of {@link PermissionDefault} to fallback to if the user does not define their own.
         */
        public static final Map<Limit, PermissionDefault> FALLBACK_DEFAULTS;

        static {
            FALLBACK_DEFAULTS = new HashMap<>();
            for (Limit limit : Limit.values()) {
                FALLBACK_DEFAULTS.put(limit, limit.fallbackDefault);
            }
        }

        public final String permissionSuffix;
        public final String description;
        public final PermissionDefault fallbackDefault;
    }
}
