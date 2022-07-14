package dev.kejona.crossplatforms.command;

import com.google.common.collect.ImmutableMap;
import dev.kejona.crossplatforms.Constants;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface CommandOrigin {

    /**
     * Mapping of Logger levels to Bukkit chat colours
     */
    Map<Logger.Level, NamedTextColor> LOGGER_COLORS = ImmutableMap.of(
            Logger.Level.INFO, NamedTextColor.WHITE,
            Logger.Level.WARN, NamedTextColor.GOLD,
            Logger.Level.SEVERE, NamedTextColor.RED);

    /**
     * Check the command origin has a permission
     * @param permission the permission to check
     * @return True if the command origin has the permission
     */
    boolean hasPermission(String permission);

    /**
     * Sends the origin the message without any processing or plugin prefix appended.
     */
    void sendRaw(TextComponent message);

    /**
     * Legacy text chars should NOT be passed to this. Sends an origin a message with the plugin prefix.
     * {@link #sendMessage(Logger.Level, String)}, {@link #warn(String)}, or {@link #severe(String)} is more preferable to use
     * if the message is a warning or severe message.
     */
    default void sendMessage(String message) {
        sendMessage(Component.text(message));
    }

    /**
     * Legacy text chars should NOT be passed to this. Sends an origin a message with the plugin prefix.
     * {@link #sendMessage(Logger.Level, String)}, {@link #warn(String)}, or {@link #severe(String)} is more preferable to use
     * if the message is a warning or severe message.
     */
    default void sendMessage(TextComponent component) {
        sendRaw(Component.text().append(Constants.MESSAGE_PREFIX).append(component).build());
    }

    /**
     * Legacy text chars should NOT be passed to this. Sends an origin a coloured warning with the plugin prefix.
     * This is essentially shortcut for {@link #sendMessage(Logger.Level, String)}, with warn level.
     */
    default void warn(String message) {
        sendMessage(Logger.Level.WARN, message);
    }

    /**
     * Legacy text chars should NOT be passed to this. Sends an origin a coloured severe message with the plugin prefix.
     * This is essentially shortcut for {@link #sendMessage(Logger.Level, String)}, with severe level.
     */
    default void severe(String message) {
        sendMessage(Logger.Level.SEVERE, message);
    }


    /**
     * Send a message with the plugin prefix, and colour.
     * @param level The Logger level (mapped to a colour if the origin is a player)
     * @param message The message to send
     */
    default void sendMessage(@Nonnull Logger.Level level, @Nonnull String message) {
        Objects.requireNonNull(level);
        Objects.requireNonNull(message);

        if (isConsole()) {
            Logger.get().log(level, message);
        } else {
            sendMessage(Component.text(message, LOGGER_COLORS.getOrDefault(level, NamedTextColor.WHITE)));
        }
    }

    /**
     * @return True if the command origin represents a player
     */
    boolean isPlayer();

    /**
     * @return True if the command origin represents the console. This is not guaranteed to be the inverse of {@link CommandOrigin#isPlayer()}.
     * For example, the CommandOrigin may be a command block, which is not a Player or Console
     */
    boolean isConsole();

    /**
     * @return The underlying implementation handle
     */
    Object getHandle();

    /**
     * Is only guaranteed to return an Optional with a present value if the CommandOrigin represents a Player
     */
    Optional<UUID> getUUID();

    /**
     * Checks if this CommandOrigin is a bedrock player through the given {@link BedrockHandler}
     * @param handler the BedrockHandler to perform the lookup with
     * @return True if the command origin is a bedrock player. false if it is anything else, such as a java player or console command sender.
     */
    default boolean isBedrockPlayer(BedrockHandler handler) {
        return isPlayer() && handler.isBedrockPlayer(getUUID().orElseThrow(AssertionError::new));
    }
}
