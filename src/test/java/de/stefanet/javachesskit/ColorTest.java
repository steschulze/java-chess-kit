package de.stefanet.javachesskit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorTest {

    @Test
    void testOther() {
        assertEquals(Color.BLACK, Color.WHITE.other());
        assertEquals(Color.WHITE, Color.BLACK.other());
    }

    @Test
    void testFromSymbol() {
        assertEquals(Color.WHITE, Color.fromSymbol('w'));
        assertEquals(Color.BLACK, Color.fromSymbol('b'));
        assertThrows(IllegalArgumentException.class, () -> Color.fromSymbol('W'));
    }

    @Test
    void testFullName() {
        assertEquals("white", Color.WHITE.fullName());
        assertEquals("black", Color.fromSymbol('b').fullName());
    }

}