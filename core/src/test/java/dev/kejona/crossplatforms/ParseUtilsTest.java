package dev.kejona.crossplatforms;

import org.junit.jupiter.api.Test;

import static dev.kejona.crossplatforms.utils.ParseUtils.downSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseUtilsTest {

    @Test
    public void testDownSize() {
        assertEquals("1.2", downSize("1.2"));
        assertEquals("3.14", downSize("3.14"));
        assertEquals("-5.56", downSize("-5.56"));

        assertEquals("3", downSize("3.0"));
        assertEquals("14", downSize("14.00"));
        assertEquals("-16", downSize("-16.000000000"));
        assertEquals("256", downSize("256.0000000000000000000"));

        assertEquals("165", downSize("165"));
        assertEquals("-100", downSize("-100"));

        assertThrows(Exception.class, () -> downSize(null));
        assertThrows(Exception.class, () -> downSize(""));
        assertThrows(Exception.class, () -> downSize(" "));
    }
}
