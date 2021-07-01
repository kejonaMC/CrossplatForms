package dev.projectg.geyserhub.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlaceholderUtils {

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @return the formatted text.
     */
    public static String setPlaceholders(@Nonnull Player player, @Nonnull String text) {
        String processedText = text;

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            processedText = PlaceholderAPI.setPlaceholders(player, text);
        } else {
            processedText = processedText.replace("%player_name%", player.getName()).replace("%player_uuid%", player.getUniqueId().toString());
        }

        return processedText;
    }
}
