package dev.kejona.crossplatforms.filler;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FillerUtils {

    @Nullable
    @Contract("_, _, null -> param1; null, _, !null -> param3; !null, _, !null -> !null")
    public static String replace(@Nullable String base, @Nonnull Pattern key, @Nullable String replacement) {
        if (replacement == null) {
            return base; // value is not present. return either an override the user specified, or null.
        } else if (base == null) {
            return replacement; // user did not specify formatting, still use the raw value
        }

        return key.matcher(base).replaceAll(Matcher.quoteReplacement(replacement));
    }}
