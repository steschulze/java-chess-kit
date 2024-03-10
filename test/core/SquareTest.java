package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @Test
    public void testEquality() {
        Square square1 = Square.fromName("b4");
        Square square2 = Square.fromName("b4");
        Square square3 = Square.fromName("b3");
        Square square4 = Square.fromName("f3");

        assertEquals(square1, square2);
        assertEquals(square2, square1);

        assertNotEquals(square1, square3);
        assertNotEquals(square1, square4);
        assertNotEquals(square3, square4);
    }

    @Test
    public void testSimpleProperties() {
        Square f7 = Square.fromName("f7");
        assertFalse(f7.isDark());
        assertTrue(f7.isLight());
        assertEquals(7, f7.getRank());
        assertEquals('f', f7.getFile());
        assertEquals("f7", f7.getName());
        assertEquals(101, f7.get0x88Index());
        assertEquals(5, f7.getX());
        assertEquals(6, f7.getY());
        assertFalse(f7.isBackrank());
    }

    @Test
    public void testCreation() {
        assertEquals(new Square(3, 5), Square.fromName("d6"));
        assertEquals(Square.from0x88Index(2), Square.fromName("c1"));
        assertEquals(Square.fromRankAndFile(2, 'g'), Square.fromName("g2"));
    }

}