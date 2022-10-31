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

    public static boolean hasChar(CharSequence s, char c) {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            if (s.charAt(i) == c) {
                return true;
            }
        }

        return false;
    }
}
