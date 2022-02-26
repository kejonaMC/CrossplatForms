package dev.projectg.crossplatforms.permission;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ConfigSerializable
public record Permission(@Nonnull String key,
                         @Nullable String description,
                         @Nonnull PermissionDefault defaultPermission) {

    @Override
    public String toString() {
        return "[" + key + ", " + description + ", " + defaultPermission + "]";
    }
}
