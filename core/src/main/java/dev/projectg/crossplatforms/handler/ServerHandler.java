package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.proxy.ProxyCommand;
import dev.projectg.crossplatforms.interfacing.java.JavaMenu;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import dev.projectg.crossplatforms.permission.Permission;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * An abstract handle on the server implementation
 */
public interface ServerHandler {

    /**
     * Get a player by their UUID
     */
    @Nullable
    FormPlayer getPlayer(UUID uuid);

    /**
     * Get a player by the name
     */
    @Nullable
    FormPlayer getPlayer(String name);

    List<FormPlayer> getPlayers();

    @NotNull
    Audience asAudience(CommandOrigin origin);

    boolean isGeyserEnabled();
    boolean isFloodgateEnabled();

    boolean isPermissionRegistered(String key);

    void registerPermission(String key, @Nullable String description, PermissionDefault def);

    default void registerPermission(Permission permission) {
        registerPermission(permission.key(), permission.description(), permission.defaultPermission());
    }

    void unregisterPermission(String key);

    /**
     * Execute a command as the server console
     * @param command The command string to execute
     */
    void dispatchCommand(DispatchableCommand command);

    void dispatchCommands(List<DispatchableCommand> commands);

    /**
     * Execute a command as the given player.
     * @param player The player to run the command as
     * @param command The command string to execute
     */
    void dispatchCommand(UUID player, DispatchableCommand command);

    void dispatchCommands(UUID player, List<DispatchableCommand> commands);


    /**
     * Register a {@link ProxyCommand}
     * @param proxyCommand The ProxyCommand to register. It's {@link CommandType} must be only {@link CommandType#INTERCEPT_CANCEL} or {@link CommandType#INTERCEPT_PASS}.
     */
    void registerProxyCommand(ProxyCommand proxyCommand);

    void clearProxyCommands();

    void sendMenu(FormPlayer formPlayer, JavaMenu menu);
}
