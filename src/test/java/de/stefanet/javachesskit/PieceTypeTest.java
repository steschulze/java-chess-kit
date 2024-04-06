package de.stefanet.javachesskit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PieceTypeTest {

	@Test
	void fromSymbol() {
		assertEquals(PieceType.PAWN, PieceType.fromSymbol('p'));
		assertEquals(PieceType.ROOK, PieceType.fromSymbol('r'));
		assertEquals(PieceType.KNIGHT, PieceType.fromSymbol('n'));
		assertEquals(PieceType.BISHOP, PieceType.fromSymbol('b'));
		assertEquals(PieceType.QUEEN, PieceType.fromSymbol('q'));
		assertEquals(PieceType.KING, PieceType.fromSymbol('k'));
	}

	@Test
	void fromSymbolUpperCase() {
		assertEquals(PieceType.PAWN, PieceType.fromSymbol('P'));
		assertEquals(PieceType.ROOK, PieceType.fromSymbol('R'));
		assertEquals(PieceType.KNIGHT, PieceType.fromSymbol('N'));
		assertEquals(PieceType.BISHOP, PieceType.fromSymbol('B'));
		assertEquals(PieceType.QUEEN, PieceType.fromSymbol('Q'));
		assertEquals(PieceType.KING, PieceType.fromSymbol('K'));
	}

	@Test
	void fromSymbolInvalid() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> PieceType.fromSymbol('x'));
		assertEquals("No piece with symbol x", exception.getMessage());
	}
}