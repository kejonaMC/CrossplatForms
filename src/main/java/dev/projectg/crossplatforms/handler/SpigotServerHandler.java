package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.proxy.ProxyCommand;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class SpigotServerHandler implements ServerHandler, Listener {

    private static final Pattern COMMAND_PATTERN = Pattern.compile("\s");
    private static final String PERMISSION_MESSAGE = Constants.MESSAGE_PREFIX + "You don't have permission to run that.";

    private final Server server;
    private final CrossplatForms plugin;

    private final Map<String, ProxyCommand> proxyCommands = new HashMap<>();

    public SpigotServerHandler(CrossplatForms plugin) {
        this.server = plugin.getServer();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

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

    @Override
    public void registerProxyCommand(ProxyCommand proxyCommand) {
        proxyCommands.put(proxyCommand.getName(), proxyCommand);
    }

    @Override
    public void clearProxyCommands() {
        proxyCommands.clear();
    }

    @EventHandler
    public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
        String name = COMMAND_PATTERN.split(event.getMessage().substring(1))[0]; // remove command slash and get first command
        Logger.getLogger().debug("preprocess command: [" + event.getMessage() + "] -> [" + name + "]");
        ProxyCommand command = proxyCommands.get(name);
        if (command != null) {
            org.bukkit.entity.Player player = event.getPlayer();
            if (player.hasPermission(command.getPermission())) {
                command.run(
                        new SpigotPlayer(player),
                        CrossplatForms.getInstance().getInterfaceManager(),
                        CrossplatForms.getInstance().getBedrockHandler()
                );
            } else {
                player.sendMessage(PERMISSION_MESSAGE);
            }

            if (command.getMethod() == CommandType.INTERCEPT_CANCEL) {
                event.setCancelled(true);
            }
        }
    }
}
