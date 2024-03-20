package de.stefanet.javachesskit.board0x88;

import de.stefanet.javachesskit.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @Test
    void testSquareEquality() {
        Square square1 = Square.parseSquare("b4");
        Square square2 = Square.parseSquare("b4");
        Square square3 = Square.parseSquare("b3");
        Square square4 = Square.parseSquare("f3");

        assertEquals(square1, square1);
        assertEquals(square1, square2);
        assertEquals(square2, square1);

        assertNotEquals(square1, square3);
        assertNotEquals(square1, square4);
        assertNotEquals(square3, square4);
        assertNotEquals(null, square1);
        assertNotEquals("b3", square3);
    }

    @Test
    void testSimpleProperties() {
        Square f7 = Square.parseSquare("f7");
        assertFalse(f7.isDark());
        assertTrue(f7.isLight());
        assertEquals(7, f7.getRank());
        assertEquals('f', f7.getFile());
        assertEquals("f7", f7.getName());
        assertEquals(101, f7.get0x88Index());
        assertEquals(5, f7.getFileIndex());
        assertEquals(6, f7.getRankIndex());
        assertFalse(f7.isBackrank());
    }

    @Test
    void testCreation() {
        assertEquals(Square.getSquare(3, 5), Square.parseSquare("d6"));
        assertEquals(Square.from0x88Index(2), Square.parseSquare("c1"));
        assertEquals(Square.getSquare(2, 'g'), Square.parseSquare("g2"));
    }

    @Test
    void testFromName_wrongLength() {
        assertThrows(IllegalArgumentException.class, () -> Square.parseSquare("abc"));
    }

    @Test
    void testFromName_wrongFile() {
        assertThrows(IllegalArgumentException.class, () -> Square.parseSquare("s6"));
    }

    @Test
    void testFromName_wrongRank() {
        assertThrows(IllegalArgumentException.class, () -> Square.parseSquare("e9"));
        assertThrows(IllegalArgumentException.class, () -> Square.parseSquare("e0"));
    }

    @Test
    void testFromRankAndFile_wrongRank() {
        assertThrows(IllegalArgumentException.class, () -> Square.getSquare(9, 'd'));
        assertThrows(IllegalArgumentException.class, () -> Square.getSquare(-1, 'd'));
    }

    @Test
    void testFromRankAndFile_wrongFile() {
        assertThrows(IllegalArgumentException.class, () -> Square.getSquare(4, 'n'));
    }

    @Test
    void testFrom0x88Index_negativeIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> Square.from0x88Index(-2));
    }

    @Test
    void testFrom0x88Index_indexOutOfBound() {
        assertThrows(IndexOutOfBoundsException.class, () -> Square.from0x88Index(135));
    }

    @Test
    void testFrom0x88Index_offBoardIndex() {
        assertThrows(IllegalArgumentException.class, () -> Square.from0x88Index(0x2B));
    }

    @Test
    void testIsLightSquare() {
        Square d1 = Square.parseSquare("d1");
        assertTrue(d1.isLight());
        assertFalse(d1.isDark());
    }

    @Test
    void testIsDarkSquare() {
        Square d8 = Square.parseSquare("d8");
        assertTrue(d8.isDark());
        assertFalse(d8.isLight());
    }

    @Test
    void testIsBackrank() {
        Square a1 = Square.parseSquare("a1");
        Square h8 = Square.parseSquare("h8");
        Square e4 = Square.parseSquare("e4");

        assertTrue(a1.isBackrank());
        assertTrue(h8.isBackrank());
        assertFalse(e4.isBackrank());
    }

    @Test
    void testToString() {
        Square square = Square.getSquare(3, 'c');
        assertEquals("Square.fromName('c3')", square.toString());
    }

    @Test
    void testHashCode() {
        Square square = Square.parseSquare("e4");
        assertEquals(0x34, square.hashCode());
    }

}