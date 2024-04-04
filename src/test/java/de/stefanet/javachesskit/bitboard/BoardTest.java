package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.*;
import de.stefanet.javachesskit.polyglot.Polyglot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
	void testAttackers() {
		Board board = new Board("r1b1k2r/pp1n1ppp/2p1p3/q5B1/1b1P4/P1n1PN2/1P1Q1PPP/2R1KB1R b Kkq - 3 10");
		SquareSet attackers = board.attackers(Color.WHITE, Square.C3);

		assertEquals(3, attackers.size());
		assertTrue(attackers.contains(Square.C1));
		assertTrue(attackers.contains(Square.D2));
		assertTrue(attackers.contains(Square.B2));

		assertFalse(attackers.contains(Square.E1));
		assertFalse(attackers.contains(Square.D4));

	}

	@Test
	void testEnPassantAttackers() {
		Board board = new Board("4k3/8/8/8/4pPp1/8/8/4K3 b - f3 0 1");
		SquareSet attackers = board.attackers(Color.BLACK, Square.F3);

		assertEquals(2, attackers.size());
		assertTrue(attackers.contains(Square.E4));
		assertTrue(attackers.contains(Square.G4));

		attackers = board.attackers(Color.BLACK, Square.F4);
		assertTrue(attackers.isEmpty());
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
		assertTrue(board.isCheck());
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

	@Test
	void testSelectiveCastling() {
		Board board = new Board("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R w KQkq - 0 1");

		assertTrue(board.generateCastlingMoves(Bitboard.ALL & ~board.kings, Bitboard.ALL).isEmpty());

		Set<Move> moves = board.generateCastlingMoves(Bitboard.ALL, Bitboard.ALL & ~Bitboard.Squares.H1);
		assertEquals(1, moves.size());
	}

	@Test
	void testCastlingRight_getsDestroyed() {
		Board board = new Board("2r1k2r/2qbbpp1/p2pp3/1p3PP1/Pn2P3/1PN1B3/1P3QB1/1K1R3R b k - 0 22");
		board.pushSan("Rxh1");
		assertEquals("2r1k3/2qbbpp1/p2pp3/1p3PP1/Pn2P3/1PN1B3/1P3QB1/1K1R3r w - - 0 23", board.getFen());
	}

	@Test
	void testInvalidCastlingRights() {
		Board board = new Board("1r2k3/8/8/8/8/8/8/R3KR2 w KQkq - 0 1");
		assertEquals("1r2k3/8/8/8/8/8/8/R3KR2 w Q - 0 1", board.getFen());

		assertTrue(board.status().contains(Status.BAD_CASTLING_RIGHTS));
		assertTrue(board.hasQueensideCastlingRights(Color.WHITE));
		assertFalse(board.hasKingsideCastlingRights(Color.WHITE));

		assertFalse(board.hasKingsideCastlingRights(Color.BLACK));
		assertFalse(board.hasQueensideCastlingRights(Color.BLACK));
	}

	@Test
	void testStatus_defaultBoard() {
		Board board = new Board();
		assertTrue(board.status().contains(Status.VALID));
		assertTrue(board.isValid());

		board.removePieceType(Square.H1);
		assertTrue(board.status().contains(Status.BAD_CASTLING_RIGHTS));

		board.removePieceType(Square.E8);
		assertTrue(board.status().contains(Status.NO_BLACK_KING));
	}

	@Test
	void testStatus_enPassant() {
		Board board = new Board();
		board.pushSan("e4");
		assertEquals(Square.E3, board.epSquare);
		assertTrue(board.isValid());

		board.removePieceType(Square.E4);
		assertTrue(board.status().contains(Status.INVALID_EP_SQUARE));
	}

	@Test
	void testStatus_badCastlingRights() {
		Board board = new Board("2rrk3/8/8/8/8/8/3PPPPP/2RK4 w KQkq - 0 1");
		assertTrue(board.status().contains(Status.BAD_CASTLING_RIGHTS));
	}

	@Test
	void testStatus_oppositeCheck() {
		Board board = new Board("4k3/8/8/8/8/8/4Q3/4K3 w - - 0 1");
		assertTrue(board.status().contains(Status.OPPOSITE_CHECK));
	}

	@Test
	void testStatus_emptyBoard() {
		Board board = new Board(null);
		assertTrue(board.status().contains(Status.EMPTY));
		assertTrue(board.status().contains(Status.NO_WHITE_KING));
		assertTrue(board.status().contains(Status.NO_BLACK_KING));
	}

	@Test
	void testStatus_tooManyKings() {
		Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBKKBNR w KQkq - 0 1");
		assertTrue(board.status().contains(Status.TOO_MANY_KINGS));
	}

	@Test
	void testStatus_tripleCheck() {
		Board board = new Board("4k3/5P2/3N4/8/8/8/4R3/4K3 b - - 0 1");
		assertTrue(board.status().contains(Status.IMPOSSIBLE_CHECK));
		assertTrue(board.status().contains(Status.TOO_MANY_CHECKERS));
	}

	@Test
	void testStatus_impossibleCheck() {
		Board board = new Board("3R4/8/q4k2/2B5/1NK5/3b4/8/8 w - - 0 1");
		assertTrue(board.status().contains(Status.IMPOSSIBLE_CHECK));

		board = new Board("2Nq4/2K5/1b6/8/7R/3k4/7P/8 w - - 0 1");
		assertTrue(board.status().contains(Status.IMPOSSIBLE_CHECK));

		board = new Board("5R2/2P5/8/4k3/8/3rK2r/8/8 w - - 0 1");
		assertTrue(board.status().contains(Status.IMPOSSIBLE_CHECK));

		board = new Board("8/8/8/1k6/3Pp3/8/8/4KQ2 b - d3 0 1");
		assertTrue(board.status().contains(Status.IMPOSSIBLE_CHECK));
	}

	@Test
	void testStatus_validCheck() {
		Board board = new Board("8/8/5k2/p1q5/PP1rp1P1/3P1N2/2RK1r2/5nN1 w - - 0 3");
		assertTrue(board.status().contains(Status.VALID));
	}

	@Test
	void testFindMove_pawnMoves() {
		Board board = new Board("4k3/1P6/8/8/8/8/3P4/4K2R w K - 0 1");
		assertEquals(Move.fromUCI("d2d4"), board.findMove(Square.D2, Square.D4));
		assertEquals(Move.fromUCI("b7b8q"), board.findMove(Square.B7, Square.B8));
		assertEquals(Move.fromUCI("b7b8r"), board.findMove(Square.B7, Square.B8, PieceType.ROOK));

	}

	@Test
	void testFindMove_illegalMoves() {
		Board board = new Board("4k3/1P6/8/8/8/8/3P4/4K2R w K - 0 1");
		assertThrows(IllegalMoveException.class, () -> board.findMove(Square.D2, Square.D8));
		assertThrows(IllegalMoveException.class, () -> board.findMove(Square.E1, Square.A1));
	}

	@Test
	void testFindMove_castling() {
		Board board = new Board("4k3/1P6/8/8/8/8/3P4/4K2R w K - 0 1");
		assertEquals(Move.fromUCI("e1g1"), board.findMove(Square.E1, Square.G1));
	}

	@ParameterizedTest
	@CsvSource({
			"false,false, rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
			"true,true,k1K1B1B1/8/8/8/8/8/8/8 w - - 7 32",
			"false,false,kbK1B1B1/8/8/8/8/8/8/8 w - - 7 32",
			"true,true, 8/5k2/8/8/8/8/3K4/8 w - - 0 1",
			"true,true, 8/3k4/8/8/2N5/8/3K4/8 b - - 0 1",
			"true,false, 8/4rk2/8/8/8/8/3K4/8 w - - 0 1",
			"true,false, 8/4qk2/8/8/8/8/3K4/8 w - - 0 1",
			"false,false, 8/4bk2/8/8/8/8/3KB3/8 w - - 0 1",
			"false,false, 8/8/3Q4/2bK4/B7/8/1k6/8 w - - 1 68",
			"true,true, 8/5k2/8/8/8/4B3/3K1B2/8 w - - 0 1",
			"true,true, 5K2/8/8/1B6/8/k7/6b1/8 w - - 0 39",
			"true,true, 8/8/8/4k3/5b2/3K4/8/2B5 w - - 0 33",
			"false,true, 3b4/8/8/6b1/8/8/R7/K1k5 w - - 0 1"
	})
	void testInsufficientMaterial(boolean white, boolean black, String fen) {
		Board board = new Board(fen);
		assertEquals(white, board.hasInsufficientMaterial(Color.WHITE));
		assertEquals(black, board.hasInsufficientMaterial(Color.BLACK));
		assertEquals(white && black, board.isInsufficientMaterial());
	}

	@Test
	void testScholarsMate() {
		Board board = new Board();
		Move e4 = Move.fromUCI("e2e4");
		assertTrue(board.legalMoves().contains(e4));
		board.push(e4);

		Move e5 = Move.fromUCI("e7e5");
		assertTrue(board.legalMoves().contains(e5));
		board.push(e5);

		Move Qf3 = Move.fromUCI("d1f3");
		assertTrue(board.legalMoves().contains(Qf3));
		board.push(Qf3);

		Move Nc6 = Move.fromUCI("b8c6");
		assertTrue(board.legalMoves().contains(Nc6));
		board.push(Nc6);

		Move Bc4 = Move.fromUCI("f1c4");
		assertTrue(board.legalMoves().contains(Bc4));
		board.push(Bc4);

		Move Rb8 = Move.fromUCI("a8b8");
		assertTrue(board.legalMoves().contains(Rb8));
		board.push(Rb8);

		assertFalse(board.isCheck());
		assertFalse(board.isCheckmate());
		assertFalse(board.isStalemate());
		assertFalse(board.isGameOver());

		Move Qf7 = Move.fromUCI("f3f7");
		assertTrue(board.legalMoves().contains(Qf7));
		board.push(Qf7);

		assertTrue(board.isCheck());
		assertTrue(board.isCheckmate());
		assertTrue(board.isGameOver());
		assertFalse(board.isStalemate());

		assertEquals("1rbqkbnr/pppp1Qpp/2n5/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQk - 0 4", board.getFen());

	}

	@Test
	void testResult_defaultBoard() {
		Board board = new Board();
		assertEquals("*", board.result(true));
	}

	@Test
	void testResult_checkmate() {
		Board board = new Board("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3");
		assertEquals("0-1", board.result(true));
	}

	@Test
	void testResult_stalemate() {
		Board board = new Board("7K/7P/7k/8/6q1/8/8/8 w - - 0 1");
		assertEquals("1/2-1/2", board.result(true));
	}

	@Test
	void testResult_insufficientMaterial() {
		Board board = new Board("4k3/8/8/8/8/5B2/8/4K3 w - - 0 1");
		assertEquals("1/2-1/2", board.result(true));
	}

	@Test
	void testResult_fiftyMoveRule() {
		Board board = new Board("4k3/8/6r1/8/8/8/2R5/4K3 w - - 120 1");
		assertEquals("*", board.result());
		assertEquals("1/2-1/2", board.result(true));
	}

	@Test
	void testResult_fiftysevenMoveRule() {
		Board board = new Board("4k3/8/6r1/8/8/8/2R5/4K3 w - - 369 1");
		assertEquals("1/2-1/2", board.result());
	}

	@Test
	void testSan_castlingWithCheck() {
		String fen = "rnbk1b1r/ppp2pp1/5n1p/4p1B1/2P5/2N5/PP2PPPP/R3KBNR w KQ - 0 7";
		Board board = new Board(fen);

		Move longCastleCheck = Move.fromUCI("e1c1");
		assertEquals("O-O-O+", board.san(longCastleCheck));
		assertEquals(fen, board.getFen());
	}

	@Test
	void testSan_enPassantMate() {
		String fen = "6bk/7b/8/3pP3/8/8/8/Q3K3 w - d6 0 2";
		Board board = new Board(fen);
		Move move = Move.fromUCI("e5d6");
		assertEquals("exd6#", board.san(move));
		assertEquals(fen, board.getFen());
	}

	@Test
	void testSan_disambiguation() {
		String fen = "N3k2N/8/8/3N4/N4N1N/2R5/1R6/4K3 w - - 0 1";
		Board board = new Board(fen);

		assertEquals("Kf1", board.san(Move.fromUCI("e1f1")));
		assertEquals("Rcc2", board.san(Move.fromUCI("c3c2")));
		assertEquals("Rbc2", board.san(Move.fromUCI("b2c2")));
		assertEquals("N4b6", board.san(Move.fromUCI("a4b6")));
		assertEquals("N8g6", board.san(Move.fromUCI("h8g6")));
		assertEquals("Nh4g6", board.san(Move.fromUCI("h4g6")));

		assertEquals(fen, board.getFen());
	}

	@Test
	void testSan_illegalMove() {
		String fen = "8/8/8/R2nkn2/8/8/2K5/8 b - - 0 1";
		Board board = new Board(fen);

		assertEquals("Ne3+", board.san(Move.fromUCI("f5e3")));
		assertEquals(fen, board.getFen());
	}

	@Test
	void testSan_promotion() {
		String fen = "7k/1p2Npbp/8/2P5/1P1r4/3b2QP/3q1pPK/2RB4 b - - 1 29";
		Board board = new Board(fen);

		assertEquals("f1=Q", board.san(Move.fromUCI("f2f1q")));
		assertEquals("f1=N+", board.san(Move.fromUCI("f2f1n")));
		assertEquals(fen, board.getFen());
	}

	@Test
	void testLan_simpleMove() {
		String fen = "N3k2N/8/8/3N4/N4N1N/2R5/1R6/4K3 w - - 0 1";
		Board board = new Board(fen);
		assertEquals("Ke1-f1", board.lan(Move.fromUCI("e1f1")));
		assertEquals("Rc3-c2", board.lan(Move.fromUCI("c3c2")));
		assertEquals("Na4-c5", board.lan(Move.fromUCI("a4c5")));
		assertEquals(fen, board.getFen());
	}

	@Test
	void testLan_capture() {
		String fen = "rnbq1rk1/ppp1bpp1/4pn1p/3p2B1/2PP4/2N1PN2/PP3PPP/R2QKB1R w KQ - 0 7";
		Board board = new Board(fen);
		assertEquals("Bg5xf6", board.lan(Move.fromUCI("g5f6")));
		assertEquals(fen, board.getFen());
	}

	@Test
	void testLan_pawnMove() {
		String fen = "6bk/7b/8/3pP3/8/8/8/Q3K3 w - d6 0 2";
		Board board = new Board(fen);
		assertEquals("e5xd6#", board.lan(Move.fromUCI("e5d6")));
		assertEquals("e5-e6+", board.lan(Move.fromUCI("e5e6")));
		assertEquals(fen, board.getFen());
	}

	@Test
	void testVariationSan_defaultBoard() {
		Board board = new Board();
		List<Move> moves = Arrays.asList(
				Move.fromUCI("e2e4"),
				Move.fromUCI("e7e5"),
				Move.fromUCI("d2d4"));
		assertEquals("1. e4 e5 2. d4", board.variationSan(moves));
	}

	@Test
	void testVariationSan_fromPosition() {
		String fen = "rn1qr1k1/1p2bppp/p3p3/3pP3/P2P1B2/2RB1Q1P/1P3PP1/R5K1 w - - 0 19";
		Board board = new Board(fen);


		List<Move> moves = Arrays.asList(
				Move.fromUCI("d3h7"), Move.fromUCI("g8h7"),
				Move.fromUCI("f3h5"), Move.fromUCI("h7g8"),
				Move.fromUCI("c3g3"), Move.fromUCI("e7f8"),
				Move.fromUCI("f4g5"), Move.fromUCI("e8e7"),
				Move.fromUCI("g5f6"), Move.fromUCI("b8d7"));

		String san = board.variationSan(moves);

		assertEquals("19. Bxh7+ Kxh7 20. Qh5+ Kg8 21. Rg3 Bf8 22. Bg5 Re7 23. Bf6 Nd7", san);
		assertEquals(fen, board.getFen());

		board.push(moves.get(0));
		san = board.variationSan(moves.subList(1, moves.size()));

		assertEquals("19...Kxh7 20. Qh5+ Kg8 21. Rg3 Bf8 22. Bg5 Re7 23. Bf6 Nd7", san);
	}

	@Test
	void testVariationSan_illegalMove() {
		String fen = "rn1qr1k1/1p2bppp/p3p3/3pP3/P2P1B2/2RB1Q1P/1P3PP1/R5K1 w - - 0 19";
		Board board = new Board(fen);

		List<Move> moves = Arrays.asList(
				Move.fromUCI("d3h7"), Move.fromUCI("g8h7"),
				Move.fromUCI("f3h6"), Move.fromUCI("h7g8"));

		assertThrows(IllegalMoveException.class, () -> board.variationSan(moves));
	}

	@Test
	void testMoveStackUsage() {
		Board board = new Board();
		board.pushUCI("d2d4");
		board.pushUCI("d7d5");
		board.pushUCI("g1f3");
		board.pushUCI("c8f5");
		board.pushUCI("e2e3");
		board.pushUCI("e7e6");
		board.pushUCI("f1d3");
		board.pushUCI("f8d6");
		board.pushUCI("e1g1");

		String san = new Board().variationSan(board.moveStack);
		assertEquals("1. d4 d5 2. Nf3 Bf5 3. e3 e6 4. Bd3 Bd6 5. O-O", san);
	}

	@Test
	void testMoveCount() {
		Board board = new Board("1N2k3/P7/8/8/3n4/8/2PP4/R3K2R w KQ - 0 1");
		assertEquals(33, board.pseudoLegalMoves().count());
	}

	@Test
	void testPolyglot() {
		Board board = new Board();

		assertEquals(Board.STARTING_FEN, board.getFen());
		assertEquals(0x463b96181691fc9cL, Polyglot.zobristHash(board));

		board.pushSan("e4");
		assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board.getFen());
		assertEquals(0x823c9b50fd114196L, Polyglot.zobristHash(board));

		board.pushSan("d5");
		assertEquals("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2", board.getFen());
		assertEquals(0x0756b94461c50fb0L, Polyglot.zobristHash(board));

		board.pushSan("e5");
		assertEquals("rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2", board.getFen());
		assertEquals(0x662fafb965db29d4L, Polyglot.zobristHash(board));

		board.pushSan("f5");
		assertEquals("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3", board.getFen());
		assertEquals(0x22a48b5a8e47ff78L, Polyglot.zobristHash(board));

		board.pushSan("Ke2");
		assertEquals("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR b kq - 1 3", board.getFen());
		assertEquals(0x652a607ca3f242c1L, Polyglot.zobristHash(board));

		board.pushSan("Kf7");
		assertEquals("rnbq1bnr/ppp1pkpp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR w - - 2 4", board.getFen());
		assertEquals(0x00fdd303c946bdd9L, Polyglot.zobristHash(board));
	}

	@Test
	void testPolyglot2() {
		Board board = new Board();
		board.pushSan("a4");
		board.pushSan("b5");
		board.pushSan("h4");
		board.pushSan("b4");
		board.pushSan("c4");

		assertEquals("rnbqkbnr/p1pppppp/8/8/PpP4P/8/1P1PPPP1/RNBQKBNR b KQkq c3 0 3", board.getFen());
		assertEquals(0x3c8123ea7b067637L, Polyglot.zobristHash(board));

		board.pushSan("bxc3");
		board.pushSan("Ra3");
		assertEquals("rnbqkbnr/p1pppppp/8/8/P6P/R1p5/1P1PPPP1/1NBQKBNR b Kkq - 1 4", board.getFen());
		assertEquals(0x5c3f9b829b279560L, Polyglot.zobristHash(board));
	}

	@Test
	void testCastlingMoveGeneration() {
		String fen = "rnbqkbnr/2pp1ppp/8/4p3/2BPP3/P1N2N2/PB3PPP/2RQ1RK1 b kq - 1 10";
		Board board = new Board(fen);
		Move illegalMove = Move.fromUCI("g1g2");

		assertFalse(board.legalMoves().contains(illegalMove));
		assertFalse(board.pseudoLegalMoves().contains(illegalMove));

		board.pushSan("exd4");

		illegalMove = Move.fromUCI("e1c1");
		assertFalse(board.legalMoves().contains(illegalMove));
		assertFalse(board.pseudoLegalMoves().contains(illegalMove));

		board.pop();

		for (Move move1 : board.pseudoLegalMoves()) {
			board.push(move1);
			for (Move move2 : board.pseudoLegalMoves()) {
				board.push(move2);
				board.pop();
			}
			board.pop();
		}

		assertEquals(fen, board.getFen());
		assertTrue((board.kings & Bitboard.Squares.G1) != 0);
		assertTrue((board.occupied & Bitboard.Squares.G1) != 0);
		assertTrue((board.whitePieces & Bitboard.Squares.G1) != 0);
		assertEquals(Piece.fromTypeAndColor(PieceType.KING, Color.WHITE), board.pieceAt(Square.G1));
		assertEquals(Piece.fromTypeAndColor(PieceType.ROOK, Color.WHITE), board.pieceAt(Square.C1));
	}

	@Test
	void testStatefulMoveGeneration() {
		Board board = new Board("r1b1k3/p2p1Nr1/n2b3p/3pp1pP/2BB1p2/P3P2R/Q1P3P1/R3K1N1 b Qq - 0 1");
		int count = 0;

		for (Move move : board.legalMoves()) {
			board.push(move);
			board.generateLegalMoves();
			count++;
			board.pop();
		}

		assertEquals(26, count);
	}

	@Test
	void testEquality() {
		assertEquals(new Board(), new Board());

		Board a = new Board();
		Board b = new Board();

		a.pushSan("e4");
		b.pushSan("d3");

		assertNotEquals(a, b);
	}

	@Test
	void testOneKingMoveGeneration() {
		Board board = Board.empty();
		board.setPiece(Square.A1, PieceType.KING, Color.WHITE);

		assertFalse(board.isValid());
		assertEquals(3, board.legalMoves().count());
		assertEquals(3, board.pseudoLegalMoves().count());

		board.pushSan("Kb1");

		assertEquals(0, board.legalMoves().count());
		assertEquals(0, board.pseudoLegalMoves().count());
	}

	@Test
	void testThreefoldRepetition() {
		Board board = new Board();

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Nf3");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Nf6");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Ng1");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Ng8"); // return to starting position

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Nf3");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Nf6");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Ng1");

		// black can return to the starting position with Ng8
		assertTrue(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pushSan("Ng8");

		assertTrue(board.canClaimThreefoldRepetition());
		assertTrue(board.isRepetition());

		board.pushSan("e4");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isRepetition());

		board.pop();

		assertTrue(board.canClaimThreefoldRepetition());

		board.pop();

		assertTrue(board.canClaimThreefoldRepetition());

		while (!board.moveStack.isEmpty()) {
			board.pop();
			assertFalse(board.canClaimThreefoldRepetition());
		}
	}

	@Test
	void testFivefoldRepetition() {
		String fen = "rnbq1rk1/ppp3pp/3bpn2/3p1p2/2PP4/2NBPN2/PP3PPP/R1BQK2R w KQ - 3 7";
		Board board = new Board(fen);

		for (int i = 0; i < 3; i++) {
			board.pushSan("Be2");
			assertFalse(board.isFivefoldRepetition());
			board.pushSan("Ne4");
			assertFalse(board.isFivefoldRepetition());
			board.pushSan("Bd3");
			assertFalse(board.isFivefoldRepetition());
			board.pushSan("Nf6");
			assertFalse(board.isFivefoldRepetition());
			assertFalse(board.isGameOver());
		}

		board.pushSan("Be2");
		assertFalse(board.isFivefoldRepetition());

		board.pushSan("Ne4");
		assertFalse(board.isFivefoldRepetition());

		board.pushSan("Bd3");
		assertFalse(board.isFivefoldRepetition());

		board.pushSan("Nf6");
		assertTrue(board.isFivefoldRepetition());
		assertTrue(board.isGameOver());

		assertTrue(board.canClaimThreefoldRepetition());

		board.pushSan("Qc2");
		board.pushSan("Qd7");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isFivefoldRepetition());

		board.pushSan("Qd2");
		board.pushSan("Qe7");

		assertFalse(board.canClaimThreefoldRepetition());
		assertFalse(board.isFivefoldRepetition());

		board.pushSan("Qd1");
		assertFalse(board.isFivefoldRepetition());
		assertFalse(board.isGameOver());
		assertTrue(board.canClaimThreefoldRepetition());
		assertTrue(board.isGameOver(true));

		board.pushSan("Qd8");

		assertTrue(board.isFivefoldRepetition());
		assertTrue(board.canClaimThreefoldRepetition());
		assertEquals(fen.split("\\s")[0], board.getFen().split("\\s")[0]);
	}

	@Test
	void testTrivialRepetition() {
		assertTrue(new Board().isRepetition(1));
	}

	@Test
	void testFiftyMoves_defaultBoard() {
		Board board = new Board();
		assertFalse(board.isFiftyMoves());
		assertFalse(board.canClaimFiftyMoveRule());
	}

	@Test
	void testFiftyMoves_fromPosition1() {
		Board board = new Board("8/5R2/8/r2KB3/6k1/8/8/8 w - - 19 79");
		assertFalse(board.isFiftyMoves());
		assertFalse(board.canClaimFiftyMoveRule());
	}

	@Test
	void testFiftyMoves_fromPosition2() {
		Board board = new Board("8/8/6r1/4B3/8/4K2k/5R2/8 b - - 68 103");
		assertFalse(board.isFiftyMoves());
		assertFalse(board.canClaimFiftyMoveRule());
	}

	@Test
	void testFiftyMoves_fromPosition3() {
		Board board = new Board("6R1/7k/8/8/1r3B2/5K2/8/8 w - - 99 119");
		assertFalse(board.isFiftyMoves());
		assertTrue(board.canClaimFiftyMoveRule());
	}

	@Test
	void testFiftyMoves_fromPosition4() {
		Board board = new Board("8/7k/8/6R1/1r3B2/5K2/8/8 b - - 100 119");
		assertTrue(board.isFiftyMoves());
		assertTrue(board.canClaimFiftyMoveRule());
	}

	@Test
	void testFiftyMoves_fromPosition5() {
		Board board = new Board("8/7k/8/1r3KR1/5B2/8/8/8 w - - 105 122");
		assertTrue(board.isFiftyMoves());
		assertTrue(board.canClaimFiftyMoveRule());
	}

	@Test
	void testFiftyMoves_checkmate() {
		Board board = new Board("k7/8/NKB5/8/8/8/8/8 b - - 105 176");
		assertFalse(board.isFiftyMoves());
		assertFalse(board.canClaimFiftyMoveRule());
	}

	@Test
	void testFiftyMoves_stalemate() {
		Board board = new Board("k7/3N4/1K6/1B6/8/8/8/8 b - - 99 1");
		assertTrue(board.isStalemate());
		assertTrue(board.isGameOver());
		assertFalse(board.isFiftyMoves());
		assertFalse(board.canClaimFiftyMoveRule());
		assertFalse(board.canClaimDraw());
	}

	@Test
	void testEnPassantLegality_white() {
		Move move = Move.fromUCI("h5g6");
		Board board = new Board("rnbqkbnr/pppppp2/7p/6pP/8/8/PPPPPPP1/RNBQKBNR w KQkq g6 0 3");

		assertTrue(board.isLegal(move));

		board.pushSan("Nf3");

		assertFalse(board.isLegal(move));

		board.pushSan("Nf6");

		assertFalse(board.isLegal(move));
	}

	@Test
	void testEnPassantLegality_black() {
		Move move = Move.fromUCI("c4d3");
		Board board = new Board("rnbqkbnr/pp1ppppp/8/8/2pP4/2P2N2/PP2PPPP/RNBQKB1R b KQkq d3 0 3");

		assertTrue(board.isLegal(move));

		board.pushSan("Qc7");

		assertFalse(board.isLegal(move));

		board.pushSan("Bd2");

		assertFalse(board.isLegal(move));
	}

	@ParameterizedTest
	@CsvSource({
			Board.STARTING_FEN,
			"rnbqkbnr/pp1ppppp/2p5/8/6P1/2P5/PP1PPP1P/RNBQKBNR b KQkq - 0 1",
			"rnb1kbnr/ppq1pppp/2pp4/8/6P1/2P5/PP1PPPBP/RNBQK1NR w KQkq - 0 1",
			"rn2kbnr/p1q1ppp1/1ppp3p/8/4B1b1/2P4P/PPQPPP2/RNB1K1NR w KQkq - 0 1",
			"rnkq1bnr/p3ppp1/1ppp3p/3B4/6b1/2PQ3P/PP1PPP2/RNB1K1NR w KQ - 0 1",
			"rn1q1bnr/3kppp1/2pp3p/pp6/1P2b3/2PQ1N1P/P2PPPB1/RNB1K2R w KQ - 0 1",
			"rnkq1bnr/4pp2/2pQ2pp/pp6/1P5N/2P4P/P2PPP2/RNB1KB1b w Q - 0 1",
			"rn3b1r/1kq1p3/2pQ1npp/Pp6/4b3/2PPP2P/P4P2/RNB1KB2 w Q - 0 1",
			"r4br1/8/k1p2npp/Ppn1p3/P7/2PPP1qP/4bPQ1/RNB1KB2 w Q - 0 1",
			"rnbqk1nr/p2p3p/1p5b/2pPppp1/8/P7/1PPQPPPP/RNB1KBNR w KQkq c6 0 1",
			"rnb1k2r/pp1p1p1p/1q1P4/2pnpPp1/6P1/2N5/PP1BP2P/R2QKBNR w KQkq e6 0 1",
			"1n4kr/2B4p/2nb2b1/ppp5/P1PpP3/3P4/5K2/1N1R4 b - c3 0 1",
			"r2n3r/1bNk2pp/6P1/pP3p2/3pPqnP/1P1P1p1R/2P3B1/Q1B1bKN1 b - e3 0 1",
	})
	void testPseudoLegality(String fen) {
		Board board = new Board(fen);

		List<Move> moves = Arrays.asList(
				new Move(Square.A2, Square.A4),
				new Move(Square.C1, Square.E3),
				new Move(Square.G8, Square.F6),
				new Move(Square.D7, Square.D8, PieceType.QUEEN),
				new Move(Square.E5, Square.E4)
		);

		Set<Move> pseudoLegalMoves = board.generatePseudoLegalMoves();

		for (Move move : pseudoLegalMoves) {
			assertTrue(board.isPseudoLegal(move));
		}

		for (Move move : moves) {
			if (!pseudoLegalMoves.contains(move)) {
				assertFalse(board.isPseudoLegal(move));
			}
		}
	}

	@Test
	void testPseudoLegalCastlingMasks() {
		Board board = new Board("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
		Move kingSide = Move.fromUCI("e1g1");
		Move queenSide = Move.fromUCI("e1c1");

		Set<Move> pseudoLegalMoves = board.generatePseudoLegalMoves();
		assertTrue(pseudoLegalMoves.contains(kingSide));
		assertTrue(pseudoLegalMoves.contains(queenSide));

		pseudoLegalMoves = board.generatePseudoLegalMoves(Bitboard.Ranks.RANK_2, Bitboard.ALL);
		assertTrue(pseudoLegalMoves.isEmpty());

		pseudoLegalMoves = board.generatePseudoLegalMoves(Bitboard.ALL, Bitboard.Squares.A1);
		assertTrue(pseudoLegalMoves.contains(queenSide));
		assertFalse(pseudoLegalMoves.contains(kingSide));
	}

	@Test
	void testPieces() {
		Board board = new Board();
		SquareSet king = board.pieces(PieceType.KING, Color.WHITE);
		assertTrue(king.contains(Square.E1));
		assertEquals(1, king.size());
	}

	@Test
	void testToString() {
		Board board = new Board("7k/1p1qn1b1/pB1p1n2/3Pp3/4Pp1p/2QN1B2/PP4PP/6K1 w - - 0 28");
		assertEquals(". . . . . . . k\n" +
					 ". p . q n . b .\n" +
					 "p B . p . n . .\n" +
					 ". . . P p . . .\n" +
					 ". . . . P p . p\n" +
					 ". . Q N . B . .\n" +
					 "P P . . . . P P\n" +
					 ". . . . . . K .", board.toString());
	}

	@Test
	void testUnicode1() {
		Board board = new Board("7k/1p1qn1b1/pB1p1n2/3Pp3/4Pp1p/2QN1B2/PP4PP/6K1 w - - 0 28");
		assertEquals("· · · · · · · ♚\n" +
					 "· ♟ · ♛ ♞ · ♝ ·\n" +
					 "♟ ♗ · ♟ · ♞ · ·\n" +
					 "· · · ♙ ♟ · · ·\n" +
					 "· · · · ♙ ♟ · ♟\n" +
					 "· · ♕ ♘ · ♗ · ·\n" +
					 "♙ ♙ · · · · ♙ ♙\n" +
					 "· · · · · · ♔ ·", board.unicode(false, false, '·', Color.WHITE));
	}

	@Test
	void testUnicode2() {
		Board board = new Board("7k/1p1qn1b1/pB1p1n2/3Pp3/4Pp1p/2QN1B2/PP4PP/6K1 w - - 0 28");
		assertEquals(" -----------------\n" +
					 "8 |·|·|·|·|·|·|·|♔|\n" +
					 " -----------------\n" +
					 "7 |·|♙|·|♕|♘|·|♗|·|\n" +
					 " -----------------\n" +
					 "6 |♙|♝|·|♙|·|♘|·|·|\n" +
					 " -----------------\n" +
					 "5 |·|·|·|♟|♙|·|·|·|\n" +
					 " -----------------\n" +
					 "4 |·|·|·|·|♟|♙|·|♙|\n" +
					 " -----------------\n" +
					 "3 |·|·|♛|♞|·|♝|·|·|\n" +
					 " -----------------\n" +
					 "2 |♟|♟|·|·|·|·|♟|♟|\n" +
					 " -----------------\n" +
					 "1 |·|·|·|·|·|·|♚|·|\n" +
					 " -----------------\n" +
					 "   a b c d e f g h", board.unicode(true, true, '·', Color.WHITE));
	}

	@Test
	void testMoveInfo() {
		Board board = new Board("r1bqkb1r/p3np2/2n1p2p/1p4pP/2pP4/4PQ1N/1P2BPP1/RNB1K2R w KQkq g6 0 11");

		assertTrue(board.isCapture(board.parseSan("Qxf7+")));
		assertFalse(board.isEnPassant(board.parseSan("Qxf7+")));
		assertFalse(board.isCastling(board.parseSan("Qxf7+")));

		assertTrue(board.isCapture(board.parseSan("hxg6")));
		assertTrue(board.isEnPassant(board.parseSan("hxg6")));
		assertFalse(board.isCastling(board.parseSan("hxg6")));

		assertFalse(board.isCapture(board.parseSan("b3")));
		assertFalse(board.isEnPassant(board.parseSan("b3")));
		assertFalse(board.isCastling(board.parseSan("b3")));

		assertFalse(board.isCapture(board.parseSan("Ra6")));
		assertFalse(board.isEnPassant(board.parseSan("Ra6")));
		assertFalse(board.isCastling(board.parseSan("Ra6")));

		assertFalse(board.isCapture(board.parseSan("0-0")));
		assertFalse(board.isEnPassant(board.parseSan("0-0")));
		assertTrue(board.isCastling(board.parseSan("0-0")));
	}

	@Test
	void testPin() {
		Board board = new Board("rnb1k1nr/2pppppp/3P4/8/1b5q/8/PPPNPBPP/RNBQKB1R w KQkq - 0 1");

		assertTrue(board.isPinned(Color.WHITE, Square.F2));
		assertTrue(board.isPinned(Color.WHITE, Square.D2));

		assertFalse(board.isPinned(Color.WHITE, Square.E1));
		assertFalse(board.isPinned(Color.BLACK, Square.H4));
		assertFalse(board.isPinned(Color.BLACK, Square.E8));

		assertEquals(Bitboard.ALL, board.pinMask(Color.WHITE, Square.B1));

		long pin;
		pin = Bitboard.Squares.E1 | Bitboard.Squares.F2 | Bitboard.Squares.G3 | Bitboard.Squares.H4;
		assertEquals(pin, board.pinMask(Color.WHITE, Square.F2));

		pin = Bitboard.Squares.E1 | Bitboard.Squares.D2 | Bitboard.Squares.C3 | Bitboard.Squares.B4 | Bitboard.Squares.A5;
		assertEquals(pin, board.pinMask(Color.WHITE, Square.D2));

		assertEquals(Bitboard.ALL, board.pinMask(Color.WHITE, Square.F7));
	}

	@Test
	void testPinInCheck() {
		Board board = new Board("1n1R2k1/2b1qpp1/p3p2p/1p6/1P2Q2P/4PNP1/P4PB1/6K1 b - - 0 1");

		assertFalse(board.isPinned(Color.BLACK, Square.B8));
		assertTrue(board.isPinned(Color.BLACK, Square.E8));
	}

	@Test
	void testImpossibleEnPassant1() {
		Board board = new Board("1b1b4/8/b1P5/2kP4/8/2b4K/8/8 w - c6 0 1");

		assertTrue(board.status().contains(Status.INVALID_EP_SQUARE));
	}

	@Test
	void testImpossibleEnPassant2() {
		Board board = new Board("5K2/8/2pp2Pp/2PP4/P5Pp/2pP1Ppp/P6p/7k b - g3 0 1");

		assertTrue(board.status().contains(Status.INVALID_EP_SQUARE));
	}

	@Test
	void testImpossibleEnPassant3() {
		Board board = new Board("8/7k/8/7p/8/8/8/K7 w - h6 0 1");

		assertTrue(board.status().contains(Status.INVALID_EP_SQUARE));
	}

	@Test
	void testHorizontallySkeweredEnPassant() {
		Board board = new Board("8/8/8/r2Pp2K/8/8/4k3/8 w - e6 0 1");
		Move move = Move.fromUCI("d5e6");

		assertTrue(board.status().contains(Status.VALID));
		assertTrue(board.isPseudoLegal(move));

		assertTrue(board.generatePseudoLegalMoves().contains(move));
		assertTrue(board.generatePseudoLegalEnPassant().contains(move));

		assertFalse(board.isLegal(move));
		assertFalse(board.generateLegalMoves().contains(move));
		assertFalse(board.generateLegalEnPassant().contains(move));
	}

	@Test
	void testDiagonallySkeweredEnPassant1() {
		Board board = new Board("2b1r2r/8/5P1k/2p1pP2/5R1P/6PK/4q3/4R3 w - e6 0 1");
		Move move = Move.fromUCI("f5e6");

		assertTrue(board.generateLegalEnPassant().contains(move));
		assertTrue(board.generateLegalMoves().contains(move));
	}

	@Test
	void testDiagonallySkeweredEnPassant2() {
		Board board = new Board("8/8/8/5k2/4Pp2/8/2B5/4K3 b - e3 0 1");
		Move move = Move.fromUCI("f4e3");

		assertTrue(board.isPseudoLegal(move));
		assertTrue(board.generatePseudoLegalEnPassant().contains(move));
		assertTrue(board.generatePseudoLegalMoves().contains(move));

		assertFalse(board.isLegal(move));
		assertFalse(board.generateLegalEnPassant().contains(move));
		assertFalse(board.generateLegalMoves().contains(move));
	}

	@Test
	void testDiagonallySkeweredEnPassant3() {
		Board board = new Board("8/8/8/7B/6Pp/8/4k2K/3r4 b - g3 0 1");
		Move move = Move.fromUCI("h4g3");

		assertTrue(board.isPseudoLegal(move));
		assertTrue(board.generatePseudoLegalEnPassant().contains(move));
		assertTrue(board.generatePseudoLegalMoves().contains(move));

		assertFalse(board.isLegal(move));
		assertFalse(board.generateLegalEnPassant().contains(move));
		assertFalse(board.generateLegalMoves().contains(move));
	}

	@Test
	void testFilePinnedEnPassant() {
		Board board = new Board("8/5K2/8/3k4/3pP3/8/8/3R4 b - e3 0 1");
		Move move = Move.fromUCI("d4e3");

		assertTrue(board.isPseudoLegal(move));
		assertTrue(board.generatePseudoLegalEnPassant().contains(move));
		assertTrue(board.generatePseudoLegalMoves().contains(move));

		assertFalse(board.isLegal(move));
		assertFalse(board.generateLegalEnPassant().contains(move));
		assertFalse(board.generateLegalMoves().contains(move));
	}

	@Test
	void testEnPassantEvasion() {
		Board board = new Board("8/8/8/2k5/2pP4/8/4K3/8 b - d3 0 1");
		Move move = Move.fromUCI("c4d3");

		assertTrue(board.isPseudoLegal(move));
		assertTrue(board.generatePseudoLegalMoves().contains(move));
		assertTrue(board.generatePseudoLegalEnPassant().contains(move));

		assertTrue(board.isLegal(move));
		assertTrue(board.generateLegalMoves().contains(move));
		assertTrue(board.generateLegalEnPassant().contains(move));
	}

	@Test
	void testCaptureGeneration() {
		Board board = new Board("3q1rk1/ppp1p1pp/4b3/3pPp2/3P4/1K1n4/PPQ2PPP/3b1BNR w - f6 0 1");

		Set<Move> legalCaptures = board.generateLegalCaptures();

		assertTrue(legalCaptures.contains(board.parseSan("Qxd1")));
		assertTrue(legalCaptures.contains(board.parseSan("exf6")));
		assertTrue(legalCaptures.contains(board.parseSan("Bxd3")));
		assertEquals(3, legalCaptures.size());

		Set<Move> pseudoLegalCaptures = board.generatePseudoLegalCaptures();

		assertTrue(pseudoLegalCaptures.contains(board.parseSan("Qxd1")));
		assertTrue(pseudoLegalCaptures.contains(board.parseSan("exf6")));
		assertTrue(pseudoLegalCaptures.contains(board.parseSan("Bxd3")));
		assertTrue(pseudoLegalCaptures.contains(Move.fromUCI("c2c7")));
		assertTrue(pseudoLegalCaptures.contains(Move.fromUCI("c2d3")));
		assertEquals(5, pseudoLegalCaptures.size());
	}

	@Test
	void testCastling_isLegal() {
		Board board = new Board("rnbqkbnr/5p2/1pp3pp/p2P4/6P1/2NPpN2/PPP1Q1BP/R3K2R w Qq - 0 11");

		assertFalse(board.isLegal(Move.fromUCI("e1g1")));

		board.castlingRights |= Bitboard.Squares.H1;

		assertTrue(board.isLegal(Move.fromUCI("e1g1")));
	}

	@Test
	void testMirror() {
		Board board = new Board("r1bq1r2/pp2n3/4N2k/3pPppP/1b1n2Q1/2N5/PP3PP1/R1B1K2R w KQ g6 0 15");
		Board mirrored = new Board("r1b1k2r/pp3pp1/2n5/1B1N2q1/3PpPPp/4n2K/PP2N3/R1BQ1R2 b kq g3 0 15");
		assertEquals(mirrored, board.mirror());
		board.applyMirror();
		assertEquals(mirrored, board);
	}
}