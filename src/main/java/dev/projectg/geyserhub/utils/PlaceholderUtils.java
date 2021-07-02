package dev.projectg.geyserhub.utils;

import dev.projectg.geyserhub.SelectorLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlaceholderUtils {

    private static final boolean usePlaceholders;
    static {
        SelectorLogger.getLogger().debug("Initializing PlaceholderUtils");
        usePlaceholders = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @return the formatted text.
     */
    public static String setPlaceholders(@Nonnull Player player, @Nonnull String text) {
        if (usePlaceholders) {
            return PlaceholderAPI.setPlaceholders(player, text);
        } else {
            return text.replace("%player_name%", player.getName()).replace("%player_uuid%", player.getUniqueId().toString());
        }
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @return the formatted text.
     */
    public static List<String> setPlaceholders(@Nonnull Player player, @Nonnull List<String> text) {
        List<String> processedText = new ArrayList<>();
        for (String line : text) {
            processedText.add(setPlaceholders(player, line));
        }
        return processedText;
    }
}
