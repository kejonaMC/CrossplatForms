package dev.kejona.crossplatforms.spigot.common.handler;

import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.CommandType;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.command.custom.InterceptCommand;
import dev.kejona.crossplatforms.command.custom.InterceptCommandCache;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class SpigotHandler extends InterceptCommandCache implements ServerHandler, Listener {

    private final Server server;
    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;
    private final ConsoleCommandSender console;

    public SpigotHandler(JavaPlugin plugin, BukkitAudiences audiences) {
        this.server = plugin.getServer();
        this.plugin = plugin;
        this.audiences = audiences;
        this.console = server.getConsoleSender();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private Player getPlayerOrThrow(UUID uuid) {
        ensurePrimaryThread();
        Player player = server.getPlayer(uuid);
        if (player == null) {
            throw new IllegalArgumentException("Failed to find a player with the following UUID: " + uuid);
        }
        return player;
    }

    @Override
    public FormPlayer getPlayer(UUID uuid) {
        ensurePrimaryThread();
        Player player = server.getPlayer(uuid);
        return (player == null) ? null : new SpigotPlayer(player);
    }

    @Override
    public FormPlayer getPlayer(String name) {
        ensurePrimaryThread();
        Player player = server.getPlayer(name);
        return (player == null) ? null : new SpigotPlayer(player);
    }

    @Override
    public Stream<FormPlayer> getPlayers() {
        ensurePrimaryThread();
        return server.getOnlinePlayers().stream().map(SpigotPlayer::new);
    }

    @Override
    public Stream<String> getPlayerNames() {
        ensurePrimaryThread();
        return server.getOnlinePlayers().stream().map(Player::getName);
    }

    @Nonnull
    @Override
    public Audience asAudience(CommandOrigin origin) {
        // BukkitAudiences is thread safe :) https://docs.adventure.kyori.net/platform/bukkit.html
        return audiences.sender((CommandSender) origin.getHandle());
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
    public void registerPermission(String key, @Nullable String description, dev.kejona.crossplatforms.permission.PermissionDefault def) {
        ensurePrimaryThread();
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

        Logger.get().debug("Registering permission " + key + " : " + perm);
        server.getPluginManager().addPermission(new Permission(key, description, perm));
    }

    @Override
    public void unregisterPermission(String key) {
        ensurePrimaryThread();
        server.getPluginManager().removePermission(new Permission(key));
    }

    @Override
    public void dispatchCommand(DispatchableCommand command) {
        Logger.get().debug("Executing [" + command + "] as console");
        server.dispatchCommand(console, command.getCommand());
    }

    @Override
    public void dispatchCommand(UUID playerId, DispatchableCommand command) {
        Player player = getPlayerOrThrow(playerId);
        dispatchCommand(player, command);
    }

    @Override
    public void dispatchCommands(UUID playerId, List<DispatchableCommand> commands) {
        Player player = getPlayerOrThrow(playerId);
        for (DispatchableCommand command : commands) {
            dispatchCommand(player, command);
        }
    }

    /**
     * Handles execution logic of how to run the command. Executes the command on the same thread
     * this method is called on. (Does not create a new Runnable).
     */
    private void dispatchCommand(Player player, DispatchableCommand command) {
        // don't need to ensure main thread here since spigot will throw its own exception if so, when dispatching commands.
        if (command.isPlayer()) {
            if (command.isOp() && !player.isOp()) {
                // only temporarily op the player if the command requires op and the player is not opped
                player.setOp(true);
                try {
                    server.dispatchCommand(player, command.getCommand());
                } finally {
                    player.setOp(false); // ensure player is deopped even if the command dispatch throws an exception
                }
            } else {
                server.dispatchCommand(player, command.getCommand());
            }
        } else {
            server.dispatchCommand(console, command.getCommand());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) // Same as DeluxeMenus
    public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
        String input = event.getMessage().substring(1); // remove command slash
        InterceptCommand command = findCommand(input);
        if (command != null) {
            Player player = event.getPlayer();
            BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
            if (command.getPlatform().matches(player.getUniqueId(), bedrockHandler)) {
                String permission = command.getPermission();
                if (permission == null || player.hasPermission(permission)) {
                    command.run(new SpigotPlayer(player));

                    if (command.getMethod() == CommandType.INTERCEPT_CANCEL) {
                        event.setCancelled(true);
                        // Deluxe menus ignores the fact the event is cancelled, so we must edit the message :((
                        event.setMessage("/GeyserMCIsCool");
                    }
                }
            }
        }
    }

    @Override
    public void executeSafely(Runnable runnable) {
        server.getScheduler().runTask(plugin, runnable);
    }

    private void ensurePrimaryThread() {
        if (!server.isPrimaryThread()) {
            throw new IllegalStateException("Method not called from primary thread, instead: " + Thread.currentThread());
        }
    }
}
