package dev.kejona.crossplatforms.utils;

import dev.kejona.crossplatforms.IllegalValueException;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Methods designed for parsing values from placeholders.
 */
public class ParseUtils {

    private ParseUtils() {

    }

    public static int getInt(@Nullable String value, String identifier) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(null, "integer", identifier);
        }

        String s = prune(value);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(s, "integer", identifier);
        }
    }

    public static int getUnsignedInt(@Nullable String value, String identifier) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(null, "non-negative integer", identifier);
        }

        String s = prune(value);
        try {
            return Integer.parseUnsignedInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(s, "non-negative integer", identifier);
        }
    }

    public static float getFloat(@Nullable String value, String identifier) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(null, "decimal number", identifier);
        }

        String s = prune(value);
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(s, "decimal number", identifier);
        }
    }

    public static boolean getBoolean(@Nullable String value, String identifier) throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(null, "boolean", identifier);
        }

        String lower = prune(value);
        switch (lower) {
            case "true":
            case "yes":
            case "on":
                return true;
            case "false":
            case "no":
            case "off":
                return false;
            default:
                throw new IllegalValueException(lower, "boolean", identifier);
        }
    }

    public static boolean getBoolean(@Nullable String value, boolean def) {
        if (value == null) {
            return def;
        }

        switch (prune(value)) {
            case "true":
            case "yes":
            case "on":
                return true;
            case "false":
            case "no":
            case "off":
                return false;
            default:
                return def;
        }
    }

    private static String prune(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }
}
