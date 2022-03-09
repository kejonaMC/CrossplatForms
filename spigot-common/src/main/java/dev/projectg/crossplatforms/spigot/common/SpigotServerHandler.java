package dev.projectg.crossplatforms.spigot.common;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.proxy.ProxyCommand;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
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
    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;

    private final Map<String, ProxyCommand> proxyCommands = new HashMap<>();

    public SpigotServerHandler(JavaPlugin plugin, BukkitAudiences audiences) {
        this.server = plugin.getServer();
        this.plugin = plugin;
        this.audiences = audiences;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public FormPlayer getPlayer(UUID uuid) {
        Player player = server.getPlayer(uuid);
        if (player == null) {
            return null;
        } else {
            return new SpigotPlayer(player);
        }
    }

    @Override
    public FormPlayer getPlayer(String name) {
        Player player = server.getPlayer(name);
        if (player == null) {
            return null;
        } else {
            return new SpigotPlayer(player);
        }
    }

    @Override
    public List<FormPlayer> getPlayers() {
        List<FormPlayer> players = new ArrayList<>();
        for (Player player : server.getOnlinePlayers()) {
            players.add(new SpigotPlayer(player));
        }
        return players;
    }

    @Nonnull
    @Override
    public Audience asAudience(CommandOrigin origin) {
        return audiences.sender((CommandSender) origin.getHandle());
    }

    @Override
    public boolean isPermissionRegistered(String key) {
        return server.getPluginManager().getPermission(key) != null;
    }

    @Override
    public boolean isGeyserEnabled() {
        return server.getPluginManager().isPluginEnabled("Geyser-Spigot");
    }

    @Override
    public boolean isFloodgateEnabled() {
        return server.getPluginManager().isPluginEnabled("floodgate");
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
    public void dispatchCommand(DispatchableCommand command) {
        Logger.getLogger().debug("Executing [" + command + "] as console");
        server.getScheduler().runTask(plugin, () -> server.dispatchCommand(server.getConsoleSender(), command.command()));
    }

    @Override
    public void dispatchCommands(List<DispatchableCommand> commands) {
        CommandSender console = server.getConsoleSender();
        server.getScheduler().runTask(plugin, () -> {
            for (DispatchableCommand command : commands) {
                Logger.getLogger().debug("Executing [" + command + "] as console");
                server.dispatchCommand(console, command.command());
            }
        });
    }

    @Override
    public void dispatchCommand(UUID playerId, DispatchableCommand command) {
        Player player = server.getPlayer(playerId);
        server.getScheduler().runTask(plugin, () -> dispatchCommand(player, command));
    }

    @Override
    public void dispatchCommands(UUID playerId, List<DispatchableCommand> commands) {
        Player player = server.getPlayer(playerId);
        server.getScheduler().runTask(plugin, () -> {
            for (DispatchableCommand command : commands) {
                dispatchCommand(player, command);
            }
        });
    }

    /**
     * Handles execution logic of how to run the command. Executes the command on the same thread
     * this method is called on. (Does not create a new Runnable).
     */
    private void dispatchCommand(Player player, DispatchableCommand command) {
        if (command.player()) {
            if (command.op()) {
                player.setOp(true);
                server.dispatchCommand(player, command.command());
                player.setOp(false);
            } else {
                server.dispatchCommand(player, command.command());
            }
        } else {
            server.dispatchCommand(server.getConsoleSender(), command.command());
        }
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
            Player player = event.getPlayer();
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
