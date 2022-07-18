package dev.kejona.crossplatforms.filler;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FillerUtils {

    public static final String PLACEHOLDER = "%raw%";
    public static final Pattern PATTERN = Pattern.compile(PLACEHOLDER, Pattern.LITERAL);

    /**
     * This will not return null as long as
     */
    @Nullable
    @Contract("_, null -> param1; null, !null -> param2; !null, !null -> !null")
    public static String replace(@Nullable String template, @Nullable String raw) {
        if (raw == null) {
            return template; // value is not present. return either an override the user specified, or null.
        } else if (template == null) {
            return raw; // user did not specify formatting, still use the raw value
        }

        return PATTERN.matcher(template).replaceAll(Matcher.quoteReplacement(raw));
    }}
