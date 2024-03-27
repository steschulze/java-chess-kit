package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	void testMoveMaking() {
		Board board = new Board();
		Move move = Move.fromUCI("e2e4");
		board.push(move);
		assertEquals(move, board.peek());
	}

	@Test
	void testFen() {
		Board board = new Board();
		assertEquals(Board.STARTING_FEN, board.getFen());

		String fen = "6k1/pb3pp1/1p2p2p/1Bn1P3/8/5N2/PP1q1PPP/6K1 w - - 0 24";
		board.setFen(fen);
		assertEquals(fen, board.getFen());

		board.push(Move.fromUCI("f3d2"));
		assertEquals("6k1/pb3pp1/1p2p2p/1Bn1P3/8/8/PP1N1PPP/6K1 b - - 0 24", board.getFen());
	}

	@Test
	void testFen_EnPassant() {
		Board board = new Board();
		board.push(Move.fromUCI("e2e4"));
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board.getFen());
	}

	@Test
	void testPawnCaptures() {
		Board board = new Board();

		// king's gambit
		board.push(Move.fromUCI("e2e4"));
		board.push(Move.fromUCI("e7e5"));
		board.push(Move.fromUCI("f2f4"));

		Move exf4 = Move.fromUCI("e5f4");
		assertTrue(board.pseudoLegalMoves().contains(exf4));
		assertTrue(board.legalMoves().contains(exf4));

		board.push(exf4);
		board.pop();
	}

	@Test
	void testSingleStepPawnMove() {
		Board board = new Board();
		Move move = Move.fromUCI("e2e3");

		assertTrue(board.pseudoLegalMoves().contains(move));
		assertTrue(board.legalMoves().contains(move));

		board.push(move);
		board.pop();

		assertEquals(Board.STARTING_FEN, board.getFen());
	}

	@Test
	void testAttacks() {
		Board board = new Board("5rk1/p5pp/2p3p1/1p1pR3/3P2P1/2N5/PP3n2/2KB4 w - - 1 26");

		SquareSet attacks = board.attacks(Square.E5);
		assertEquals(11, attacks.size());

		assertTrue(attacks.contains(Square.D5));
		assertTrue(attacks.contains(Square.E1));
		assertTrue(attacks.contains(Square.F5));

		assertFalse(attacks.contains(Square.E5));
		assertFalse(attacks.contains(Square.C5));
		assertFalse(attacks.contains(Square.F4));

		SquareSet pawnAttacks = board.attacks(Square.B2);
		assertTrue(pawnAttacks.contains(Square.A3));
		assertFalse(pawnAttacks.contains(Square.B3));

		assertTrue(board.attacks(Square.H1).isEmpty());
	}

	@Test
	void testClear() {
		Board board = new Board();
		board.clear();

		assertEquals(Color.WHITE, board.turn);
		assertEquals(1, board.fullMoveNumber);
		assertEquals(0, board.halfMoveClock);

		assertEquals(Bitboard.EMPTY, board.castlingRights);
		assertNull(board.epSquare);

		assertNull(board.pieceAt(Square.E1));

	}

	@Test
	void testAmbiguousMove() {
		Board board = new Board("8/8/1n6/3R1P2/1n6/2k2K2/3p4/r6r b - - 0 82");
		assertThrows(AmbiguousMoveException.class, () -> board.parseSan("Rf1"));
		assertThrows(AmbiguousMoveException.class, () -> board.parseSan("Nd5"));
	}

	@Test
	void testSan_newline() {
		Board board = new Board("rnbqk2r/ppppppbp/5np1/8/8/5NP1/PPPPPPBP/RNBQK2R w KQkq - 2 4");
		assertThrows(InvalidMoveException.class, () -> board.parseSan("0-0\n"));
		assertThrows(InvalidMoveException.class, () -> board.parseSan("Nc3\n"));
	}

	@Test
	void testSan_pawnCapture_withoutFile() {
		Board board = new Board("2rq1rk1/pb2bppp/1p2p3/n1ppPn2/2PP4/PP3N2/1B1NQPPP/RB3RK1 b - - 4 13");
		assertThrows(IllegalMoveException.class, () -> board.parseSan("c4"));
	}

	@Test
	void testSan_enPassant_withoutFile() {
		Board board = new Board("4k3/8/8/4Pp2/8/8/8/4K3 w - f6 0 2");
		assertThrows(IllegalMoveException.class, () -> board.parseSan("f6"));
	}

	@Test
	void testPromotion_withCheck() {
		Board board = new Board("8/8/8/3R1P2/8/2k2K2/3p4/r7 b - - 0 82");
		board.pushSan("d1=Q+");
		assertEquals("8/8/8/3R1P2/8/2k2K2/8/r2q4 w - - 0 83", board.getFen());
	}

	@Test
	void testCleanCastlingRights() {
		Board board = new Board();
		board.setBoardFen("k6K/8/8/pppppppp/8/8/8/QqQq4");

		assertEquals(Bitboard.EMPTY, board.cleanCastlingRights());
		assertEquals("k6K/8/8/pppppppp/8/8/8/QqQq4 w - - 0 1", board.getFen());

		board.pushSan("Qxc5");

		assertEquals(Bitboard.EMPTY, board.cleanCastlingRights());
		assertEquals("k6K/8/8/ppQppppp/8/8/8/Qq1q4 b - - 0 1", board.getFen());
	}

	@Test
	void testFen_enPassant() {
		Board board = new Board();
		board.pushSan("e4");
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board.getFen());
	}

	@Test
	void testPly() {
		Board board = new Board();
		assertEquals(0, board.ply());
		board.pushSan("e4");
		assertEquals(1, board.ply());
		board.pushSan("e5");
		assertEquals(2, board.ply());
		board.clearStack();
		assertEquals(2, board.ply());
		board.pushSan("Nf3");
		assertEquals(3, board.ply());
	}

	@Test
	void testCastling() {
		Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 1 1");
		Move move = board.parseSan("O-O");

		assertEquals(Move.fromUCI("e1g1"), move);
		assertTrue(board.legalMoves().contains(move));

		board.push(move);

		move = board.parseSan("O-O-O");

		assertEquals(Move.fromUCI("e8c8"), move);
		assertTrue(board.legalMoves().contains(move));

		board.push(move);

		assertEquals("2kr3r/8/8/8/8/8/8/R4RK1 w - - 3 2", board.getFen());

		board.pop();
		board.pop();
		assertEquals("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 1 1", board.getFen());
	}

	@Test
	void testCastlingSan() {
		Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 1 1");
		assertEquals(Move.fromUCI("e1g1"), board.parseSan("O-O"));
		assertThrows(IllegalMoveException.class, () -> board.parseSan("Kg1"));
		assertThrows(IllegalMoveException.class, () -> board.parseSan("Kh1"));
	}

}