package de.stefanet.javachesskit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

}