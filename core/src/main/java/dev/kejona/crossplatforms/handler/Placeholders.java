package dev.kejona.crossplatforms.handler;

import dev.kejona.crossplatforms.Resolver;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Placeholders {

    String setPlaceholders(@Nonnull FormPlayer player, @Nonnull String text);

    default Resolver resolver(FormPlayer player) {
        return new PlayerResolver(player, this);
    }

    /**
     * Returns the inputted text with placeholders set, if PlaceholderAPI is loaded. If not, it returns the same text.
     * @param player The player
     * @param text The text
     * @return the formatted text.
     */
    @Nonnull
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
    @Nonnull
    default String setPlaceholders(@Nonnull FormPlayer player, @Nonnull String text, @Nonnull Map<String, @Nullable String> additional) {
        if (text.isEmpty()) {
            return text;
        }

        String resolved = text;
        if (!additional.isEmpty()) {
            for (String key : additional.keySet()) {
                resolved = resolved.replace(key, additional.getOrDefault(key, ""));
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
    @Nonnull
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

    @AllArgsConstructor
    class PlayerResolver implements Resolver {

        private final FormPlayer player;
        private final Placeholders placeholders;

        @NotNull
        @Override
        public String apply(@NotNull String s) {
            return placeholders.setPlaceholders(player, s);
        }

        @NotNull
        @Override
        public String apply(@NotNull String s, @NotNull Map<String, @Nullable String> additionalPlaceholders) {
            return placeholders.setPlaceholders(player, s, additionalPlaceholders);
        }
    }
}
