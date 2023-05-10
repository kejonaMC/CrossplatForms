package dev.kejona.crossplatforms.interfacing;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.command.defaults.OpenCommand;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.permission.PermissionDefault;
import dev.kejona.crossplatforms.resolver.MapResolver;
import dev.kejona.crossplatforms.resolver.Resolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@ToString
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class Interface {

    @Inject
    private transient Placeholders placeholders;

    @Getter
    // Stuff that is generated after deserialization, once the identifier has been loaded
    private transient Map<Interface.Limit, Permission> permissions;

    @Getter
    @NodeKey
    @Required
    protected String identifier;

    @Getter
    protected String title = "";

    private Map<Interface.Limit, PermissionDefault> permissionDefaults = Collections.emptyMap();

    @Getter
    private List<Argument> arguments = Collections.emptyList();

    @Getter
    private transient String argumentSyntax = "";

    @PostProcess
    protected void postProcess() {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(identifier);
        for (Argument arg : arguments) {
            joiner.add(OpenCommand.required(arg.identifier()));
        }
        argumentSyntax = joiner.toString();
    }

    public void send(FormPlayer recipient, Map<String, String> args) throws ArgumentException {
        Resolver resolver = placeholders.resolver(recipient);
        if (arguments.isEmpty()) {
            send(recipient, resolver);
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        for (Argument def : arguments) {
            placeholders.put(def.placeholder(), def.validate(args.get(def.identifier())));
        }
        send(recipient, new MapResolver(placeholders).then(resolver));
    }

    public void send(FormPlayer recipient, @Nullable String... args) throws ArgumentException {
        Resolver resolver = placeholders.resolver(recipient);
        if (arguments.isEmpty()) {
            send(recipient, resolver);
            return;
        }
        if (args == null || arguments.size() != args.length) {
            throw new ArgumentException("Incorrect number of arguments, should be " + arguments.size());
        }

        Map<String, String> placeholders = new HashMap<>();
        int i = 0;
        for (Argument def : arguments) {
            placeholders.put(def.placeholder(), def.validate(args[i]));
            i++;
        }
        send(recipient, new MapResolver(placeholders).then(resolver));
    }

    protected abstract void send(@Nonnull FormPlayer recipient, @Nonnull Resolver resolver);

    /**
     * e.g. "crossplatforms.form."
     */
    protected abstract String getPermissionBase();

    public String permission(Interface.Limit limit) {
        return permissions.get(limit).key();
    }

    public void generatePermissions(InterfaceConfig registry) {
        if (permissions != null) {
            Logger.get().severe("Permissions in menu or form '" + identifier + "' have already been generated!");
        }

        String mainPermission = getPermissionBase() + identifier;

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

    @RequiredArgsConstructor
    public enum Limit {
        USE(".use", "Base permission to use the form or menu", PermissionDefault.TRUE),
        COMMAND(".command", "Open the form or menu through the open command", PermissionDefault.OP);

        public final String permissionSuffix;
        public final String description;
        public final PermissionDefault fallbackDefault;
    }
}
