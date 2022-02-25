package dev.projectg.crossplatforms.spigot.handler;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlaceholderAPIHandler implements PlaceholderHandler {

    public PlaceholderAPIHandler() {
        if (!PlaceholderAPI.isRegistered("player")) {
            Logger.getLogger().warn("PlaceholderAPI is installed but the Player extension is not installed! %player_name% and %player_uuid% will NOT be resolved. Please install the Player extension.");
        }
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @return the formatted text.
     */
    public String setPlaceholders(@Nonnull FormPlayer player, @Nonnull String text) {
        if (text.isBlank()) {
            return text;
        }

        return PlaceholderAPI.setPlaceholders((Player) player.getHandle(), text);
    }
}
