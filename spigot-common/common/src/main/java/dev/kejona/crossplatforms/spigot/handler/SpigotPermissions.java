package dev.kejona.crossplatforms.spigot.handler;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.permission.PermissionDefault;
import dev.kejona.crossplatforms.permission.Permissions;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

@RequiredArgsConstructor
public class SpigotPermissions implements Permissions {

    private final JavaPlugin plugin;
    private final Server server = Bukkit.getServer();

    private boolean booting = true;

    protected void registerPermission(Permission permission) {
        org.bukkit.permissions.Permission perm = toBukkit(permission);
        Logger.get().debug("Registering permission " + perm.getName() + " : " + perm.getDefault());
        server.getPluginManager().removePermission(perm);
        server.getPluginManager().addPermission(perm);
    }

    @Override
    public void registerPermissions(Collection<Permission> permissions) {
        SpigotHandler.ensurePrimaryThread(); // todo: could be run in sync task if not
        if (permissions.isEmpty()) {
            return;
        }

        if (booting) {
            // Server hasn't started yet, just block until we are done
            for (Permission perm : permissions) {
                registerPermission(perm);
            }
        } else {
            // Register 5 permissions every tick: https://github.com/kejonaMC/CrossplatForms/issues/128
            new RegisterTask(permissions, 5).runTaskTimer(plugin, 0L, 1L);
        }
    }

    @Override
    public void notifyPluginLoaded() {
        booting = false;
    }

    public static org.bukkit.permissions.Permission toBukkit(Permission p) {
        return new org.bukkit.permissions.Permission(p.key(), p.description(), toBukkit(p.defaultPermission()));
    }

    public static org.bukkit.permissions.PermissionDefault toBukkit(PermissionDefault def) {
        switch (def) {
            case TRUE:
                return org.bukkit.permissions.PermissionDefault.TRUE;
            case OP:
                return org.bukkit.permissions.PermissionDefault.OP;
            default:
                return org.bukkit.permissions.PermissionDefault.FALSE;
        }
    }

    private class RegisterTask extends BukkitRunnable {

        private final Queue<Permission> queue;
        private final int batchSize;

        public RegisterTask(Collection<Permission> permissions, int batchSize) {
            this.queue = new LinkedList<>(permissions);
            this.batchSize = batchSize;
        }

        @Override
        public void run() {
            for (int i = 0; i < batchSize && !queue.isEmpty(); i++) {
                registerPermission(queue.remove());
            }

            if (queue.isEmpty()) {
                cancel(); // finished registering all permissions
            }
        }
    }
}
