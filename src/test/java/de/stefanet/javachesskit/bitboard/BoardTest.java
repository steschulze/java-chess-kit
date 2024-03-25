package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BoardTest {

	@Test
	void testDefaultBoard() {
		Board board = new Board();
		assertEquals(Board.STARTING_FEN, board.getFen());
		assertEquals(Color.WHITE, board.turn);
		assertEquals(Piece.fromTypeAndColor(PieceType.BISHOP, Color.WHITE), board.pieceAt(Square.F1));
	}

	@Test
	void testEmptyBoard() {
		Board board = new Board(null);
		assertEquals("8/8/8/8/8/8/8/8 w - - 0 1", board.getFen());
	}

	@Test
	void testGetSet() {
		Board board = new Board();
		assertEquals(new Piece('N'), board.pieceAt(Square.B1));

		board.removePiece(Square.E2);
		assertNull(board.pieceAt(Square.E2));

		board.set(Square.E4, new Piece('R'));
		assertEquals(new Piece('R'), board.pieceAt(Square.E4));

		board.removePiece(Square.F1);
		assertNull(board.pieceAt(Square.F1));
	}

	@Test
	void testColorAt() {
		Board board = new Board();
		assertEquals(Color.WHITE, board.colorAt(Square.A1));
		assertEquals(Color.BLACK, board.colorAt(Square.A8));
		assertNull(board.colorAt(Square.E4));
	}

	@Test
	void testPseudoLegalMoveGeneration() {
		Board board = new Board("8/2R1P3/8/2pp4/2k1r3/P7/8/1K6 w - - 1 55");
		Set<Move> moves = board.generatePseudoLegalMoves();
		assertEquals(16, moves.size());
	}

	@Test
	void testPseudoLegalMoveGeneration_default() {
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

	@Test
	void testLegalMoveGeneration_default() {
		Board board = new Board();
		Set<Move> moves = board.generateLegalMoves();
		assertEquals(20, moves.size());
	}

}