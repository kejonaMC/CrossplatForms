package dev.kejona.crossplatforms.utils;

public class StringUtils {

    private StringUtils() {

    }

    public static String repeatChar(char c, int amount) {
        return new String(new char[amount]).replace('\0', c);
    }

    public static String repeatString(CharSequence chars, int amount) {
        return new String(new char[amount]).replace("\0", chars);
    }
}
