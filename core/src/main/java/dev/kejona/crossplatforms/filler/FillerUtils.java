package dev.kejona.crossplatforms.filler;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class FillerUtils {

    /**
     * Replaces part(s) of a "template" string with a given value. This is primarily meant for formatting defaultValue
     * with the optional template.
     *
     * @param template the template string to apply the key Pattern to
     * @param key the Pattern used for doing replacement in the template String
     * @param defaultValue The String that matches will be replaced with
     * @return a new string with the replacements performed if both template and default value are not null.
     * If defaultValue is null, template is returned. If template is null, it is considered that
     * not formatting required, so defaultValue is returned.
     */
    @Nullable
    @Contract("_, _, null -> param1; null, _, !null -> param3; !null, _, !null -> !null")
    public static String replace(@Nullable String template, @Nonnull Pattern key, @Nullable String defaultValue) {
        if (defaultValue == null) {
            return template; // value is not present. return either an override the user specified, or null.
        } else if (template == null) {
            return defaultValue; // user did not specify formatting, still use the raw value
        }

        return key.matcher(template).replaceAll(Matcher.quoteReplacement(defaultValue));
    }

    protected static <E> void addAtIndex(Stream<E> src, List<E> dest, int index) {
        if (index < 0) {
            // index invalid or not set - silently ignore for config purposes
            src.forEachOrdered(dest::add);
        } else {
            src.forEachOrdered(e -> dest.add(index, e));
        }
    }
}
