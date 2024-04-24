package de.stefanet.javachesskit;

import static de.stefanet.javachesskit.Bitboard.Squares.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import java.util.Collections;

class BaseBoardTest {

	@Test
	void testDefaultBoard() {
		BaseBoard board = new BaseBoard();

		assertEquals(new Piece('R'), board.get(Square.A1));
		assertEquals(new Piece('Q'), board.get(Square.D1));
		assertEquals(new Piece('p'), board.get(Square.E7));
		assertEquals(new Piece('n'), board.get(Square.G8));
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
	void testInvalidFEN_containsSpace() {
        Exception exception = assertThrows(InvalidFenException.class,
                                           () -> new BaseBoard("rnbqkbnr/pppppppp/8/8/8/8 /PPPPPPPP/RNBQKBNR"));
		assertEquals("Invalid board FEN: Contains space", exception.getMessage());
	}

	@Test
	void testInvalidFEN_tooManyRows() {
        Exception exception = assertThrows(InvalidFenException.class,
                                           () -> new BaseBoard("rnbqkbnr/pppppppp/8/8/8/8/8/PPPPPPPP/RNBQKBNR"));
		assertEquals("Invalid board FEN: Expected 8 rows in board fen", exception.getMessage());
	}

	@Test
	void testInvalidFEN_multipleNumbers() {
        Exception exception = assertThrows(InvalidFenException.class,
                                           () -> new BaseBoard("rnbqkbnr/pppppp1p/8/42p1/8/8/PPPPPPPP/RNBQKBNR"));
		assertEquals("Invalid board FEN:: Several numbers in a row", exception.getMessage());
	}

	@Test
	void testInvalidFEN_invalidCharacter() {
        Exception exception = assertThrows(InvalidFenException.class,
                                           () -> new BaseBoard("rnbqkbnr/pppppppa/8/8/8/8/PPPPPPPP/RNBQKBNR"));
		assertEquals("Invalid board FEN:: Invalid character a", exception.getMessage());
	}

	@Test
	void testInvalidFEN_invalidRowLength() {
        Exception exception = assertThrows(InvalidFenException.class,
                                           () -> new BaseBoard("rnbqkbnr/ppppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"));
		assertEquals("Invalid board FEN: Invalid row length", exception.getMessage());
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
		assertNull(board.get(Square.E4));
	}

	@Test
	void testPieceAt() {
		BaseBoard board = new BaseBoard();
		Piece piece = board.pieceAt(Square.D1);
		assertNotNull(piece);
		assertEquals(PieceType.QUEEN, piece.getType());
		assertEquals(Color.WHITE, piece.getColor());
	}


	@Test
	void testPieceTypeAt() {
		BaseBoard board = new BaseBoard();
		PieceType pieceType = board.pieceTypeAt(Square.C8);
		assertNotNull(pieceType);
		assertEquals(PieceType.BISHOP, pieceType);
	}

	@Test
	void testKingSquare() {
		BaseBoard board = new BaseBoard();
		Square square = board.getKingSquare(Color.WHITE);

		assertEquals(Square.E1, square);
	}

	@Test
	void testPieceMap() {
		BaseBoard board1 = new BaseBoard();
		BaseBoard board2 = new BaseBoard(null);

		board2.setPieceMap(board1.getPieceMap());
		assertEquals(board1, board2);

		board2.setPieceMap(Collections.emptyMap());
		assertNotEquals(board1, board2);
	}

	@Test
	void testGetters() {
		BaseBoard board = new BaseBoard();

		assertEquals(Bitboard.CORNERS, board.getRooks());
		assertEquals(B1 | G1 | B8 | G8, board.getKnights());
		assertEquals(C1 | F1 | C8 | F8, board.getBishops());

		assertEquals(D1 | D8, board.getQueens());
		assertEquals(E1 | E8, board.getKings());

		assertEquals(Bitboard.EMPTY, board.getPromoted());
		assertEquals(Bitboard.Ranks.RANK_1 |
					 Bitboard.Ranks.RANK_2 |
					 Bitboard.Ranks.RANK_7 |
					 Bitboard.Ranks.RANK_8, board.getOccupied());
	}

	@Test
	void testSetPiece_withNull() {
		BaseBoard board = new BaseBoard();
		board.setPiece(Square.E1, null, Color.WHITE);

		assertNull(board.get(Square.E1));
	}

}