package dev.kejona.crossplatforms.handler;

import dev.kejona.crossplatforms.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nullable;
import java.util.UUID;

public interface FormPlayer {

    /**
     * @return The player's UUID
     */
    UUID getUuid();

    /**
     * @return The player's name
     */
    String getName();

    /**
     * Check the player has a permission
     * @param permission the permission to check
     * @return True if the player has the permission
     */
    boolean hasPermission(String permission);

    @Nullable
    String getEncodedSkinData();

    /**
     * Sends a player the message without any processing or plugin prefix appended.
     */
    void sendRaw(Component component);

    /**
     * Legacy text chars should NOT be passed to this. Sends a player a message with the plugin prefix.
     */
    default void sendMessage(String message) {
        sendMessage(Component.text(message));
    }

    /**
     * Legacy text chars should NOT be passed to this. Sends a player a message with the plugin prefix.
     */
    default void sendMessage(TextComponent component) {
        sendRaw(Component.text().append(Constants.MESSAGE_PREFIX).append(component).build());
    }

    /**
     * Legacy text chars should NOT be passed to this. Sends a player a coloured warning with the plugin prefix.
     */
    default void warn(String message) {
        sendMessage(Component.text(message).color(NamedTextColor.GOLD));
    }

    /**
     * Switch this player to a different backend server behind a proxy like BungeeCord or Velocity. If no proxy is
     * present, this method should fail silently and return true. This method should only return false if no backend
     * server is found by the given server name.
     *
     * @param server The identifier of the backend server to switch the player to
     * @return false if and only if there is no backend server by the given name.
     */
    boolean switchBackendServer(String server); // todo: probably would be better in ServerHandler

    <T> T getHandle(Class<T> asType) throws ClassCastException;
}
