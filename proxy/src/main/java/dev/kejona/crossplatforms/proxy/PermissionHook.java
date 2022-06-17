package dev.kejona.crossplatforms.proxy;

import dev.kejona.crossplatforms.permission.PermissionDefault;
import org.jetbrains.annotations.Nullable;

public interface PermissionHook {

    static PermissionHook empty() {
        return Empty.INSTANCE;
    }

    void registerPermission(String key, @Nullable String description, PermissionDefault def);

    final class Empty implements PermissionHook {

        private static final PermissionHook INSTANCE = new Empty();

        @Override
        public void registerPermission(String key, @Nullable String description, PermissionDefault def) {
            //no-op
        }
    }
}
