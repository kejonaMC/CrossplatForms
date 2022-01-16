package dev.projectg.crossplatforms.interfacing;

import com.google.common.collect.ImmutableMap;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.Collections;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class Interface {

    @NodeKey
    @Required
    protected String identifier;

    protected String title = "";

    private Map<Interface.Limit, PermissionDefault> permissionDefaults = Collections.emptyMap();

    // Stuff that is generated after deserialization, once the identifier has been loaded
    private transient Map<Interface.Limit, Permission> permissions;

    /**
     * e.g. "crossplatforms.form"
     */
    protected abstract String getPermissionBase();

    public void generatePermissions(InterfaceRegistry registry) {
        if (permissions != null) {
            Logger.getLogger().severe("Permissions in menu or form " + identifier + " have already been generated!");
            Thread.dumpStack();
        }

        String mainPermission = getPermissionBase() + "." + identifier;

        ImmutableMap.Builder<Interface.Limit, Permission> builder = ImmutableMap.builder();
        for (Interface.Limit limit : Interface.Limit.values()) {
            // Alright this is a bit janky. 1st, attempt to retrieve the permission default from this specific config.
            // If it is not specified for this item, then we check the global permission defaults.
            // If the user has not specified anything in the globals, then we use fallback values
            PermissionDefault permissionDefault = permissionDefaults.getOrDefault(limit, registry.getGlobalPermissionDefaults().getOrDefault(limit, limit.fallbackDefault));
            builder.put(limit, new Permission(mainPermission + limit.permissionSuffix, limit.description, permissionDefault));
        }

        permissions = builder.build();
    }

    public String permission(Interface.Limit limit) {
        return permissions.get(limit).key();
    }

    @RequiredArgsConstructor
    public enum Limit {
        USE(".use", "Base permission to use the form or menu", PermissionDefault.TRUE),
        COMMAND(".command", "Open the form or menu through the open command", PermissionDefault.OP);

        /**
         * Map of {@link PermissionDefault} to fallback to if the user does not define their own.
         */
        public static final Map<Interface.Limit, PermissionDefault> FALLBACK_DEFAULTS;

        static {
            ImmutableMap.Builder<Interface.Limit, PermissionDefault> builder = ImmutableMap.builder();
            for (Interface.Limit limit : Interface.Limit.values()) {
                builder.put(limit, limit.fallbackDefault);
            }
            FALLBACK_DEFAULTS = builder.build();
        }

        public final String permissionSuffix;
        public final String description;
        public final PermissionDefault fallbackDefault;
    }
}
