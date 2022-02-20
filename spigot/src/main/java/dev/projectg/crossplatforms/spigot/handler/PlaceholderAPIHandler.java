package dev.projectg.crossplatforms.spigot.handler;

import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.handler.Player;
import dev.projectg.crossplatforms.utils.PlaceholderHandler;
import me.clip.placeholderapi.PlaceholderAPI;

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
    public String setPlaceholders(@Nonnull Player player, @Nonnull String text) {
        if (text.isBlank()) {
            return text;
        }

        return PlaceholderAPI.setPlaceholders((org.bukkit.entity.Player) player, text);
    }
}
