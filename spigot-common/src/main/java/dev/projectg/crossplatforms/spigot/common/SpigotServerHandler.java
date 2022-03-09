package dev.projectg.crossplatforms.spigot.common;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.proxy.CustomCommand;
import dev.projectg.crossplatforms.command.proxy.ProxyCommandCache;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotServerHandler extends ProxyCommandCache implements ServerHandler, Listener {

    private final Server server;
    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;

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
        return server.getOnlinePlayers().stream().map(SpigotPlayer::new).collect(Collectors.toList());
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
        PermissionDefault perm;
        switch (def) {
            case TRUE:
                perm = PermissionDefault.TRUE;
                break;
            case OP:
                perm = PermissionDefault.OP;
                break;
            default:
                perm = PermissionDefault.FALSE;
                break;
        }

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
        server.getScheduler().runTask(plugin, () -> server.dispatchCommand(server.getConsoleSender(), command.getCommand()));
    }

    @Override
    public void dispatchCommands(List<DispatchableCommand> commands) {
        CommandSender console = server.getConsoleSender();
        server.getScheduler().runTask(plugin, () -> {
            for (DispatchableCommand command : commands) {
                Logger.getLogger().debug("Executing [" + command + "] as console");
                server.dispatchCommand(console, command.getCommand());
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
        if (command.isPlayer()) {
            if (command.isOp()) {
                player.setOp(true);
                server.dispatchCommand(player, command.getCommand());
                player.setOp(false);
            } else {
                server.dispatchCommand(player, command.getCommand());
            }
        } else {
            server.dispatchCommand(server.getConsoleSender(), command.getCommand());
        }
    }

    @EventHandler
    public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
        String name = COMMAND_PATTERN.split(event.getMessage().substring(1))[0]; // remove command slash and get first command
        Logger.getLogger().debug("preprocess command: [" + event.getMessage() + "] -> [" + name + "]");
        CustomCommand command = proxyCommands.get(name);
        if (command != null) {
            Player player = event.getPlayer();
            CommandType type = command.getMethod();
            if (player.hasPermission(command.getPermission())) {
                command.run(
                        new SpigotPlayer(player),
                        CrossplatForms.getInstance().getInterfaceManager(),
                        CrossplatForms.getInstance().getBedrockHandler()
                );
            } else {
                if (type == CommandType.INTERCEPT_CANCEL) {
                    player.sendMessage(PERMISSION_MESSAGE);
                }
            }

            if (type == CommandType.INTERCEPT_CANCEL) {
                event.setCancelled(true);
            }
        }
    }
}
