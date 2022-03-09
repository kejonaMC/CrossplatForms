package dev.projectg.crossplatforms.permission;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Permission {

    @Required
    @Nonnull
    private String key;

    @Nullable
    private String description;

    @Required
    @Nonnull
    private PermissionDefault defaultPermission;

    public Permission(@NotNull String key, @NotNull String description, @NotNull PermissionDefault permissionDefault) {
        this.key = key;
        this.description = description;
        this.defaultPermission = permissionDefault;
    }

    public String key() {
        return key;
    }
    public String description() {
        return description;
    }

    public PermissionDefault defaultPermission() {
        return defaultPermission;
    }

    @Override
    public String toString() {
        return "[" + key + ", " + description + ", " + defaultPermission + "]";
    }
}
