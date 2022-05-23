package dev.projectg.crossplatforms.proxy;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PermissionHook {

    static PermissionHook empty() {
        return Empty.INSTANCE;
    }

    void registerPermission(String key, @Nullable String description, PermissionDefault def);

    void executeWithOperator(UUID uuid, Runnable runnable);

    final class Empty implements PermissionHook {

        private static final PermissionHook INSTANCE = new Empty();

        @Override
        public void registerPermission(String key, @Nullable String description, PermissionDefault def) {
            //no-op
        }

        @Override
        public void executeWithOperator(UUID uuid, Runnable runnable) {
            Logger.getLogger().warn("Cannot elevate " + uuid.toString() + " to operator without a permissions hook");
            runnable.run();
        }
    }
}
