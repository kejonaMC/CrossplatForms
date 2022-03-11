package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.proxy.CustomCommand;
import dev.projectg.crossplatforms.permission.Permission;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import net.kyori.adventure.audience.Audience;

import javax.annotation.Nonnull;
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

    @Nonnull
    Audience asAudience(CommandOrigin origin);

    boolean isGeyserEnabled();
    boolean isFloodgateEnabled();

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

    default void dispatchCommands(List<DispatchableCommand> commands) {
        commands.forEach(this::dispatchCommand);
    }

    /**
     * Execute a command. The command should only be run as the given player if {@link DispatchableCommand#isPlayer()}
     * returns true.
     * @param player The player to possibly run the command as
     * @param command The command string to execute
     */
    void dispatchCommand(UUID player, DispatchableCommand command);

    default void dispatchCommands(UUID player, List<DispatchableCommand> commands) {
        commands.forEach(c -> dispatchCommand(player, c));
    }


    /**
     * Register a {@link CustomCommand} as a proxy command. It's expected that the implementation actually deals with
     * any command executions that match the criteria of given CustomCommand.
     * @param proxyCommand The command to register. It's {@link CommandType} must be only {@link CommandType#INTERCEPT_CANCEL} or {@link CommandType#INTERCEPT_PASS}.
     */
    void registerProxyCommand(CustomCommand proxyCommand);

    /**
     * Clear any previously registered {@link CustomCommand} registered as proxy commands.
     */
    void clearProxyCommands();
}
