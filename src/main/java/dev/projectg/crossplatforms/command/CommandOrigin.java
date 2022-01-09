package dev.projectg.crossplatforms.command;

import com.google.common.collect.ImmutableMap;
import dev.projectg.crossplatforms.Logger;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface CommandOrigin {

    Map<Logger.Level, ChatColor> LOGGER_COLORS = ImmutableMap.of(
            Logger.Level.INFO, ChatColor.RESET,
            Logger.Level.WARN, ChatColor.GOLD,
            Logger.Level.SEVERE, ChatColor.RED);

    boolean hasPermission(String permission);
    void sendRaw(String message);

    /**
     * @return True if the command origin represents a player
     */
    boolean isPlayer();

    /**
     * @return True if the command origin represents the console. This is not guaranteed to be the inverse of {@link CommandOrigin#isPlayer()}.
     * For example, the CommandOrigin may be a command block, which is not a Player or Console
     */
    boolean isConsole();

    Object getHandle();

    /**
     * Is only guaranteed to return an Optional with a present value if the CommandOrigin represents a Player
     */
    Optional<UUID> getUUID();

    default void sendMessage(@Nonnull Logger.Level level, @Nonnull String message) {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);

        if (isConsole()) {
            Logger.getLogger().log(level, message);
        } else {
            // todo: abstract chat colour
            sendRaw("[CrossplatForms] " + LOGGER_COLORS.getOrDefault(level, ChatColor.RESET) + message);
        }
    }
}
