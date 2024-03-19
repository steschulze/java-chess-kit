package de.stefanet.javachesskit.bitboard;

import org.junit.jupiter.api.Test;

import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.A1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.parseSquare;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BitboardTest {

	@Test
	void testParseSquare() {
		long square = Bitboard.Squares.parseSquare("a1");
		assertEquals(A1, square);
	}

	@Test
	void testParseSquare_wrongFormat() {
		assertThrows(IllegalArgumentException.class, () -> parseSquare("a0"));
		assertThrows(IllegalArgumentException.class, () -> parseSquare("a9"));
		assertThrows(IllegalArgumentException.class, () -> parseSquare("A4"));
	}

	@Test
	void testParseSquare_wrongLength() {
		assertThrows(IllegalArgumentException.class, () -> parseSquare("d49"));
	}

}