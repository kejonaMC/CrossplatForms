package dev.projectg.crossplatforms.utils;

import dev.projectg.crossplatforms.Logger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceholderUtils {

    private static final boolean usePAPI;
    static {
        Logger.getLogger().debug("Initializing PlaceholderUtils");
        usePAPI = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        if (usePAPI && !PlaceholderAPI.isRegistered("player")) {
            Logger.getLogger().warn("PlaceholderAPI is installed but the Player extension is not installed! %player_name% and %player_uuid% will NOT be resolved. Please install the Player extension.");
        }
    }

    private PlaceholderUtils() {
        // no instantiation
        // todo: this must be converted into a manger of sorts, for different implementations.
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @return the formatted text.
     */
    public static String setPlaceholders(@Nonnull Player player, @Nonnull String text) {
        if (text.isBlank()) {
            return text;
        }

        if (usePAPI) {
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
        if (text.isEmpty()) {
            return processedText;
        }

        for (String line : text) {
            processedText.add(setPlaceholders(player, line));
        }
        return processedText;
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @param additional Additional placeholders to apply
     * @return the formatted text.
     */
    public static String setPlaceholders(@Nonnull Player player, @Nonnull String text, @Nonnull Map<String, String> additional) {
        if (text.isBlank()) {
            return text;
        }

        String resolved = text;

        if (!additional.isEmpty()) {
            for (String key : additional.keySet()) {
                if (resolved.contains(key)) {
                    resolved = resolved.replace(key, additional.get(key));
                }
            }
        }

        if (usePAPI) {
            return PlaceholderAPI.setPlaceholders(player, resolved);
        } else {
            return resolved.replace("%player_name%", player.getName()).replace("%player_uuid%", player.getUniqueId().toString());
        }
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @param additional Additional placeholders to apply
     * @return the formatted text.
     */
    public static List<String> setPlaceholders(@Nonnull Player player, @Nonnull List<String> text, Map<String, String> additional) {
        List<String> processedText = new ArrayList<>();
        if (text.isEmpty()) {
            return processedText;
        }

        for (String line : text) {
            processedText.add(setPlaceholders(player, line, additional));
        }
        return processedText;
    }
}
