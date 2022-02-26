package dev.projectg.crossplatforms.handler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PlaceholderHandler {

    String setPlaceholders(@Nonnull FormPlayer player, @Nonnull String text);

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @return the formatted text.
     */
    default List<String> setPlaceholders(@Nonnull FormPlayer player, @Nonnull List<String> text) {
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
    default String setPlaceholders(@Nonnull FormPlayer player, @Nonnull String text, @Nonnull Map<String, String> additional) {
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

        return setPlaceholders(player, resolved);
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @param additional Additional placeholders to apply
     * @return the formatted text.
     */
    default List<String> setPlaceholders(@Nonnull FormPlayer player, @Nonnull List<String> text, Map<String, String> additional) {
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
