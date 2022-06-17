package dev.projectg.crossplatforms.handler;

import dev.projectg.crossplatforms.Constants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

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

    /**
     * Sends a player the message without any processing or plugin prefix appended.
     */
    void sendRaw(TextComponent component);

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

    Object getHandle();
}
