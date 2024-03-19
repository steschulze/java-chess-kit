package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.Color;
import de.stefanet.javachesskit.Piece;
import de.stefanet.javachesskit.PieceType;
import de.stefanet.javachesskit.bitboard.Bitboard.Squares;
import org.junit.jupiter.api.Test;

import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.*;
import static org.junit.jupiter.api.Assertions.*;

class BaseBoardTest {

	@Test
	void testDefaultBoard() {
		BaseBoard board = new BaseBoard();

		assertEquals(new Piece('R'), board.get(Squares.parseSquare("a1")));
		assertEquals(new Piece('Q'), board.get(Squares.parseSquare("d1")));
		assertEquals(new Piece('p'), board.get(Squares.parseSquare("e7")));
		assertEquals(new Piece('n'), board.get(Squares.parseSquare("g8")));
	}

	@Test
	void testFEN_defaultBoard() {
		BaseBoard board = new BaseBoard();
		assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", board.getBoardFen());
	}

	@Test
	void testFEN_randomPosition() {
		BaseBoard board = new BaseBoard("r1bqkbnr/pp3ppp/2np4/1N2p3/4P3/8/PPP2PPP/RNBQKB1R");
		assertEquals("r1bqkbnr/pp3ppp/2np4/1N2p3/4P3/8/PPP2PPP/RNBQKB1R", board.getBoardFen());
	}

	@Test
	void testFEN_emptyBoard() {
		BaseBoard board = new BaseBoard(null);
		assertEquals("8/8/8/8/8/8/8/8", board.getBoardFen());
	}

	@Test
	void testPieceMask_pawns() {
		BaseBoard board = new BaseBoard();
		assertEquals(0xFF00, board.pieceMask(PieceType.PAWN, Color.WHITE));
		assertEquals(0xFF000000000000L, board.pieceMask(PieceType.PAWN, Color.BLACK));
	}

	@Test
	void testPieceMask_rooks() {
		BaseBoard board = new BaseBoard();
		assertEquals(A1 | H1, board.pieceMask(PieceType.ROOK, Color.WHITE));
		assertEquals(A8 | H8, board.pieceMask(PieceType.ROOK, Color.BLACK));
	}

	@Test
	void testPieceMask_knights() {
		BaseBoard board = new BaseBoard();
		assertEquals(B1 | G1, board.pieceMask(PieceType.KNIGHT, Color.WHITE));
		assertEquals(B8 | G8, board.pieceMask(PieceType.KNIGHT, Color.BLACK));
	}

	@Test
	void testPieceMask_bishops() {
		BaseBoard board = new BaseBoard();
		assertEquals(C1 | F1, board.pieceMask(PieceType.BISHOP, Color.WHITE));
		assertEquals(C8 | F8, board.pieceMask(PieceType.BISHOP, Color.BLACK));
	}

	@Test
	void testPieceMask_queens() {
		BaseBoard board = new BaseBoard();
		assertEquals(D1, board.pieceMask(PieceType.QUEEN, Color.WHITE));
		assertEquals(D8, board.pieceMask(PieceType.QUEEN, Color.BLACK));
	}

	@Test
	void testPieceMask_kings() {
		BaseBoard board = new BaseBoard();
		assertEquals(E1, board.pieceMask(PieceType.KING, Color.WHITE));
		assertEquals(E8, board.pieceMask(PieceType.KING, Color.BLACK));
	}

	@Test
	void testPieceMask_randomPosition() {
		BaseBoard board = new BaseBoard("8/8/3kn3/8/8/3KP3/1B6/8");
		assertEquals(D3, board.pieceMask(PieceType.KING, Color.WHITE));
		assertEquals(D6, board.pieceMask(PieceType.KING, Color.BLACK));
		assertEquals(B2, board.pieceMask(PieceType.BISHOP, Color.WHITE));
		assertEquals(E6, board.pieceMask(PieceType.KNIGHT, Color.BLACK));
		assertEquals(E3, board.pieceMask(PieceType.PAWN, Color.WHITE));
	}

	@Test
	void testCopyBoard() {
		BaseBoard board = new BaseBoard("8/8/3kn3/8/8/3KP3/1B6/8");
		BaseBoard copy = board.copy();

		assertNotSame(copy, board);
		assertEquals("8/8/3kn3/8/8/3KP3/1B6/8", copy.getBoardFen());
	}

	@Test
	void testGet_emptySquare() {
		BaseBoard board = new BaseBoard();
		assertNull(board.get(Squares.parseSquare("e4")));
	}

	@Test
	void testPieceAt() {
		BaseBoard board = new BaseBoard();
		Piece piece = board.pieceAt(Squares.parseSquare("d1"));
		assertNotNull(piece);
		assertEquals(PieceType.QUEEN, piece.getType());
		assertEquals(Color.WHITE, piece.getColor());
	}


	@Test
	void testPieceTypeAt() {
		BaseBoard board = new BaseBoard();
		PieceType pieceType = board.pieceTypeAt(Squares.parseSquare("c8"));
		assertNotNull(pieceType);
		assertEquals(PieceType.BISHOP, pieceType);
	}

	@Test
	void testKingSquare() {
		BaseBoard board = new BaseBoard();
		long square = board.getKingSquare(Color.WHITE);

		assertEquals(E1, square);
	}

}