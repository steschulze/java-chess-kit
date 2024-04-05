package de.stefanet.javachesskit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegalMoveGeneratorTest {

	@Test
	void testCount() {
		assertEquals(20, new Board().legalMoves().count());
	}

	@Test
	void testToString() {
		Board board = new Board("r3k1nr/ppq1pp1p/2p3p1/8/1PPR4/2N5/P3QPPP/5RK1 b kq b3 0 16");
		LegalMoveGenerator moveGenerator = board.legalMoves();
		assertTrue(moveGenerator.toString().contains("Qxh2+"));
	}

	@Test
	void testAny_defaultBoard() {
		Board board = new Board();
		LegalMoveGenerator moveGenerator = new LegalMoveGenerator(board);
		assertTrue(moveGenerator.any());
	}

	@Test
	void testAny_emptyBoard() {
		Board board = new Board(null);
		LegalMoveGenerator moveGenerator = new LegalMoveGenerator(board);
		assertFalse(moveGenerator.any());
	}

	@Test
	void testAny_oneLegalMove() {
		Board board = new Board("8/8/8/8/1q6/8/2k5/K7 w - - 0 1");
		LegalMoveGenerator moveGenerator = new LegalMoveGenerator(board);
		assertEquals(1, moveGenerator.count());

		// call any twice
		assertTrue(moveGenerator.any());
		assertTrue(moveGenerator.any());
	}

}