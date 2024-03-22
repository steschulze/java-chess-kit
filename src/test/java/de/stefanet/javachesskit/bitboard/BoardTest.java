package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.Move;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardTest {

	@Test
	void testPseudoLegalMoves() {
		Board board = new Board();

		Set<Move> moves = board.generatePseudoLegalMoves();
		assertEquals(20, moves.size());

		Set<Move> expectedMoves = new HashSet<>();

		for (char file = 'a'; file <= 'h'; file++) {
			String singlePawnMove = file + "2" + file + "3";
			String doublePawnMove = file + "2" + file + "4";
			expectedMoves.add(Move.fromUCI(singlePawnMove));
			expectedMoves.add(Move.fromUCI(doublePawnMove));
		}
		expectedMoves.add(Move.fromUCI("b1a3"));
		expectedMoves.add(Move.fromUCI("b1c3"));
		expectedMoves.add(Move.fromUCI("g1f3"));
		expectedMoves.add(Move.fromUCI("g1h3"));

		assertEquals(expectedMoves, moves);
	}

}