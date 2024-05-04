package de.stefanet.javachesskit.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefanet.javachesskit.Board;
import org.junit.jupiter.api.Test;

class PseudoLegalMoveGeneratorTest {

	@Test
	void testCount() {
		assertEquals(20, new Board().pseudoLegalMoves().count());
	}

	@Test
	void testToString() {
		Board board = new Board("r3k1nr/ppq1pp1p/2p3p1/8/1PPR4/2N5/P3QPPP/5RK1 b kq b3 0 16");
		PseudoLegalMoveGenerator moveGenerator = board.pseudoLegalMoves();

		assertTrue(moveGenerator.toString().contains("Qxh2+"));
		assertTrue(moveGenerator.toString().contains("e8d7"));
	}

	@Test
	void testAny_defaultBoard() {
		Board board = new Board();
		PseudoLegalMoveGenerator moveGenerator = board.pseudoLegalMoves();
		assertTrue(moveGenerator.any());
	}

	@Test
	void testAny_emptyBoard() {
		Board board = new Board(null);
		PseudoLegalMoveGenerator moveGenerator = board.pseudoLegalMoves();
		assertFalse(moveGenerator.any());
	}

	@Test
	void testAny_fromPosition() {
		Board board = new Board("8/8/8/8/1q6/8/2k5/K7 w - - 0 1");
		PseudoLegalMoveGenerator moveGenerator = board.pseudoLegalMoves();
		assertEquals(3, moveGenerator.count());

		assertTrue(moveGenerator.any());
	}

}