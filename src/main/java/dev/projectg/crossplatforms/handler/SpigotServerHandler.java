package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.Logger;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SpigotServerHandler implements ServerHandler {

    private final Server server;
    private final JavaPlugin plugin;

    @Override
    public Player getPlayer(UUID uuid) {
        org.bukkit.entity.Player player = server.getPlayer(uuid);
        if (player == null) {
            return null;
        } else {
            return new SpigotPlayer(player);
        }
    }

    @Override
    public Player getPlayer(String name) {
        org.bukkit.entity.Player player = server.getPlayer(name);
        if (player == null) {
            return null;
        } else {
            return new SpigotPlayer(player);
        }
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (org.bukkit.entity.Player player : server.getOnlinePlayers()) {
            players.add(new SpigotPlayer(player));
        }
        return players;
    }

    @Override
    public boolean isPluginEnabled(String id) {
        return server.getPluginManager().isPluginEnabled(id);
    }

    @Override
    public boolean isPermissionRegistered(String key) {
        return server.getPluginManager().getPermission(key) != null;
    }

    @Override
    public void registerPermission(String key, @Nullable String description, dev.projectg.crossplatforms.permission.PermissionDefault def) {
        PermissionDefault perm = switch (def) {
            case TRUE -> PermissionDefault.TRUE;
            case FALSE -> PermissionDefault.FALSE;
            case OP -> PermissionDefault.OP;
        };

        Logger.getLogger().debug("Registering permission " + key + " : " + perm);
        server.getPluginManager().addPermission(new Permission(key, description, perm));
    }

    @Override
    public void unregisterPermission(String key) {
        server.getPluginManager().removePermission(new Permission(key));
    }

    @Override
    public void dispatchCommand(String command) {
        dispatchCommand(server.getConsoleSender(), command);
    }

    @Override
    public void dispatchCommand(UUID player, String command) {
        CommandSender sender = server.getPlayer(player);
        if (sender == null) {
            throw new IllegalArgumentException("A player with UUID " + player + " was not found to dispatch a command as");
        } else {
            dispatchCommand(sender, command);
        }
    }

    /**
     * Executes a command synchronously, as required by the Spigot API.
     */
    private void dispatchCommand(CommandSender commandSender, String command) {
        Logger.getLogger().debug("Executing [" + command + "] as " + commandSender.getName());
        server.getScheduler().runTask(plugin, () -> server.dispatchCommand(commandSender, command));
    }
}
