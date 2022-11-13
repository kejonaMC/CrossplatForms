package dev.kejona.crossplatforms.permission;

import java.util.Collection;

public interface Permissions {

    void registerPermissions(Collection<Permission> permissions);

    default void notifyPluginLoaded() {

    }

    static Permissions empty() {
        return EmptyPermissions.INSTANCE;
    }
}
