package de.stefanet.javachesskit.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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

    @Test
    void testSymbol() {
        assertEquals('w', Color.WHITE.getSymbol());
        assertEquals('b', Color.BLACK.getSymbol());
    }

}