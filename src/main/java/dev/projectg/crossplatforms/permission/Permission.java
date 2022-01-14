package dev.projectg.crossplatforms.permission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record Permission(@Nonnull String key,
                         @Nullable String description,
                         @Nonnull DefaultPermission defaultPermission) {
}
