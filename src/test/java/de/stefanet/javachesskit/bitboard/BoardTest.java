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
}