package dev.kejona.crossplatforms.utils;

import dev.kejona.crossplatforms.IllegalValueException;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.BooleanSupplier;

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

    /**
     * Converts a float to an integer if it can be done without loss of precision.
     * @param floaty The float, in string representation.
     * @return the integer as a string if conversion was possible, otherwise the floaty parameter
     * @throws NumberFormatException if the parameter could not be parsed to a float
     */
    public static String downSize(String floaty) throws NumberFormatException {
        float value = Float.parseFloat(floaty);
        if ((int) value == value) {
            return Integer.toString((int) value);
        } else {
            return floaty;
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

    /**
     * Requires the string value to be a float, that is positive. It must not be zero or negative.
     */
    public static float getPositiveFloat(@Nullable String value, String identifier) throws IllegalValueException {
        float f = getFloat(value, identifier);
        if (f <= 0) {
            throw new IllegalValueException(value, "positive decimal number", identifier);
        }
        return f;
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

    public static boolean getBoolean(@Nullable String value, BooleanSupplier def) {
        if (value == null) {
            return def.getAsBoolean();
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
                return def.getAsBoolean();
        }
    }

    private static String prune(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }
}
