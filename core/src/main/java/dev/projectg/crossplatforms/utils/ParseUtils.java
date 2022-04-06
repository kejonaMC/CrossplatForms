package dev.projectg.crossplatforms.utils;

import dev.projectg.crossplatforms.interfacing.bedrock.custom.IllegalValueException;

import java.util.Locale;

public class ParseUtils {

    private ParseUtils() {

    }

    public static int getInt(String value, String identifier) throws IllegalValueException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(value, "integer", identifier);
        }
    }

    public static int getUnsignedInt(String value, String identifier) throws IllegalValueException {
        try {
            return Integer.parseUnsignedInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(value, "non-negative integer", identifier);
        }
    }

    public static float getFloat(String value, String identifier) throws IllegalValueException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new IllegalValueException(value, "decimal number", identifier);
        }
    }

    public static boolean getBoolean(String value, String identifier) throws IllegalValueException {
        String lower = value.toLowerCase(Locale.ROOT);
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
}
