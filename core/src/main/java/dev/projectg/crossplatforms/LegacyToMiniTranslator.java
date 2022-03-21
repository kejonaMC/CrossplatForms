package dev.projectg.crossplatforms;

import java.util.HashMap;
import java.util.Map;

public class LegacyToMiniTranslator {

    private static final Map<Character, String> TRANSLATIONS = new HashMap<>();

    static {
        TRANSLATIONS.put('0', "<black>");
    }

    private final char legacyChar;

    private LegacyToMiniTranslator(char legacyChar) {
        this.legacyChar = legacyChar;
    }

    public String translateToMini(String input) {
        final StringBuilder result = new StringBuilder(); // intermediate
        final char[] chars = input.toCharArray(); // original input as chars
        final int maxIndex = input.length() - 1; // max index

        int skip = 0; // amount of following chars to skip
        // this looks until the second last index, as formatting char would have to be at the last index
        for (int i = 0; i < maxIndex; i++) {
            if (skip > 0) {
                skip--;
                continue;
            }

            char flag = chars[i];
            if (flag == legacyChar) {
                // hit formatting marker
                // look for formatting codes after the marker
                // this looks until the last index if necessary/possible
                for (int j = i + 1; j <= maxIndex; j++) {
                    char code = chars[j]; // colour or formatting code
                    String replacement = TRANSLATIONS.get(code);
                    if (replacement == null) {
                        // no more codes after the current marker
                        break;
                    }
                    skip++; // skip this char on the next iteration (parent loop)
                    result.append(replacement);
                }
            } else {
                // something else
                result.append(flag);
            }
        }
        return result.toString();
    }

    public String translateToMini2(String input) {
        final StringBuilder result = new StringBuilder(); // intermediate
        final char[] chars = input.toCharArray(); // original input as chars
        final int maxIndex = input.length() - 1; // max index

        boolean expectingCodes = false;
        // this looks until the second last index, as formatting char would have to be at the last index
        for (int i = 0; i < maxIndex; i++) {
            char c = chars[i];
            if (expectingCodes) {
                // Next char can be a formatting code
                String replacement = TRANSLATIONS.get(c);
                if (replacement == null) {
                    // not a code, end expecting codes
                    expectingCodes = false;
                    result.append(c);
                } else {
                    // char was a code, add replacement
                    result.append(replacement);
                }
            } else if (c == legacyChar) {
                // char is the start of a formatting sequence. expect codes after this
                expectingCodes = true;
            } else {
                // anything else, non formatting
                result.append(c);
            }
        }
        return result.toString();
    }
}
