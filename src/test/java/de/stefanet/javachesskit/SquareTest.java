package de.stefanet.javachesskit;

import org.junit.jupiter.api.Test;

import static de.stefanet.javachesskit.Square.*;
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
		assertEquals(Square.getSquare('g', 2), Square.parseSquare("g2"));
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
		Square square = Square.getSquare('c', 3);
		assertEquals("C3", square.toString());
	}

	@Test
	void testSquareDistance() {
		assertEquals(0, distance(A1, A1));
		assertEquals(7, distance(A1, H8));
		assertEquals(7, distance(E1, E8));
		assertEquals(7, distance(A4, H4));
		assertEquals(1, distance(D4, E5));
	}

	@Test
	void testSquareManhattanDistance() {
		assertEquals(0, manhattanDistance(A1, A1));
		assertEquals(14, manhattanDistance(A1, H8));
		assertEquals(7, manhattanDistance(E1, E8));
		assertEquals(7, manhattanDistance(A4, H4));
		assertEquals(2, manhattanDistance(D4, E5));
	}

	@Test
	void testSquareKnightDistance() {
		assertEquals(0, knightDistance(A1, A1));
		assertEquals(6, knightDistance(A1, H8));
		assertEquals(1, knightDistance(G1, F3));
		assertEquals(5, knightDistance(E1, E8));
		assertEquals(5, knightDistance(A4, H4));
		assertEquals(3, knightDistance(A1, B1));
		assertEquals(4, knightDistance(A1, C3));
		assertEquals(4, knightDistance(A1, B2));
		assertEquals(2, knightDistance(C1, B2));
	}

}