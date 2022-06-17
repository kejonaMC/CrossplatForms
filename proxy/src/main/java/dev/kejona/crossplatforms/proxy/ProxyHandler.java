package dev.kejona.crossplatforms.proxy;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.command.custom.InterceptCommandCache;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.permission.PermissionDefault;
import org.jetbrains.annotations.Nullable;

public abstract class ProxyHandler extends InterceptCommandCache implements ServerHandler {

    private final PermissionHook permissionHook;

    public ProxyHandler(PermissionHook permissionHook) {
        this.permissionHook = permissionHook;
        if (permissionHook instanceof PermissionHook.Empty) {
            Logger.get().warn("Install LuckPerms in order for permission defaults to take effect.");
        }
    }

    @Override
    public void registerPermission(String key, @Nullable String description, PermissionDefault def) {
        permissionHook.registerPermission(key, description, def);
    }

    @Override
    public void unregisterPermission(String key) {
        // this will only have to be implemented if we had a hook for some permission system that
        // complains about registering the same permission twice
    }
}
