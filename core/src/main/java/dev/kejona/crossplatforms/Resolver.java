package dev.kejona.crossplatforms;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

/**
 * A placeholder resolver for a given player
 */
public interface Resolver extends Function<String, String> {

    @Nonnull
    String apply(@Nonnull String s);

    @Nonnull
    String apply(@Nonnull String s, @Nonnull Map<String, @Nullable String> additionalPlaceholders);

    static Resolver of(Function<String, String> keyMapper) {
        return new Resolver() {
            @NotNull
            @Override
            public String apply(@NotNull String s) {
                return keyMapper.apply(s);
            }

            @NotNull
            @Override
            public String apply(@NotNull String s, @NotNull Map<String, @Nullable String> additionalPlaceholders) {
                String result = s;
                for (String key : additionalPlaceholders.keySet()) {
                    result = result.replace(key, additionalPlaceholders.getOrDefault(key, ""));
                }
                return keyMapper.apply(result);
            }
        };
    }
}
