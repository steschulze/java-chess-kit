package de.stefanet.javachesskit.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColorTest {

    @Test
    void testOther() {
        assertEquals(Color.BLACK, Color.WHITE.other());
        assertEquals(Color.WHITE, Color.BLACK.other());
    }

}