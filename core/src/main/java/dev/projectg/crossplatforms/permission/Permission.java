package dev.projectg.crossplatforms.permission;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
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

    @Override
    public String toString() {
        return "[" + key + ", " + description + ", " + defaultPermission + "]";
    }
}
