package dev.kejona.crossplatforms;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegacyToMiniTranslator {

    private static final Map<Character, String> TRANSLATIONS = new HashMap<>();
    private static final List<Character> DECORATIONS = ImmutableList.of('k', 'l', 'm', 'n', 'o');
    private static final Map<Character, String> CLOSERS = new HashMap<>();
    private static final char RESET = 'r';

    static {
        // todo
        TRANSLATIONS.put('0', "<black>");
        CLOSERS.put('k', "<\\obf>");
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
        List<Character> open = new ArrayList<>(); // need to close everything after a reset
        List<Character> openDecorations = new ArrayList<>(); // need to close all decorations after a new colour
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
                    if (DECORATIONS.contains(c)) {
                        // decoration character
                        openDecorations.add(c);
                    } else if (c == RESET) {
                        // close all previous colours/decorations
                        for (char format : open) {
                            result.append(CLOSERS.get(format));
                        }
                        open.clear(); // everything has been closed
                        openDecorations.clear();
                        break; // No further action required
                    } else {
                        // color char, need to end all previous decorations
                        for (char decor : openDecorations) {
                            result.append(CLOSERS.get(decor));
                        }
                    }

                    open.add(c);
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
