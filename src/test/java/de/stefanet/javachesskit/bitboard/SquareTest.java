package de.stefanet.javachesskit.bitboard;

import org.junit.jupiter.api.Test;

import static de.stefanet.javachesskit.bitboard.Square.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SquareTest {

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