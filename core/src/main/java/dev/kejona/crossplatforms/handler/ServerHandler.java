package dev.kejona.crossplatforms.handler;

import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.CommandType;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.command.custom.CustomCommand;
import dev.kejona.crossplatforms.command.custom.InterceptCommand;
import net.kyori.adventure.audience.Audience;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * An abstract handle on the server implementation
 */
public interface ServerHandler {

    Comparator<FormPlayer> PLAYER_COMPARATOR = Comparator.comparing(FormPlayer::getName, String.CASE_INSENSITIVE_ORDER);

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

    Stream<FormPlayer> getPlayers();

    default Stream<FormPlayer> getPlayersSorted() {
        return getPlayers().sorted(PLAYER_COMPARATOR);
    }

    /**
     * Overriding the default implementation of this will likely be faster if {@link FormPlayer} is not used.
     */
    default Stream<String> getPlayerNames() {
        return getPlayers().map(FormPlayer::getName);
    }

    @Nonnull
    Audience asAudience(CommandOrigin origin);

    boolean isGeyserEnabled();
    boolean isFloodgateEnabled();

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
    void registerInterceptCommand(InterceptCommand proxyCommand);

    /**
     * Clear any previously registered {@link CustomCommand} registered as proxy commands.
     */
    void clearInterceptCommands();

    /**
     * Execute the given runnable in a manner that allows for thread safe access to the server API of the given implementation.
     * The default implementation is simply running the runnable immediately
     * @param runnable The runnable to execute
     */
    default void executeSafely(Runnable runnable) {
        runnable.run();
    }
}
