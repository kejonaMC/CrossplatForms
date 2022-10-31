package dev.kejona.crossplatforms.util;

import org.junit.jupiter.api.Test;

import static dev.kejona.crossplatforms.utils.StringUtils.hasChar;
import static dev.kejona.crossplatforms.utils.StringUtils.repeatChar;
import static dev.kejona.crossplatforms.utils.StringUtils.repeatString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTest {

    @Test
    public void testRepeatChar() {
        assertEquals(repeatChar('5', 0), "");
        assertEquals(repeatChar('a', 3), "aaa");
    }
    @Test
    public void testRepeatString() {
        assertEquals(repeatString("5", 0), "");
        assertEquals(repeatString("a", 3), "aaa");

        assertEquals(repeatString("555", 0), "");
        assertEquals(repeatString("aa", 3), "aaaaaa");
    }

    @Test
    public void testHasChar() {
        assertFalse(hasChar("", '7'));
        assertFalse(hasChar("abcd", 'e'));
        assertFalse(hasChar("True", '§'));

        assertTrue(hasChar("§6True", '§'));
        assertTrue(hasChar("abcd", 'b'));
    }
}
