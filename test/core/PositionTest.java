package core;

import javafx.geometry.Pos;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testFEN() {
        Position position = new Position();
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        position.setFen(fen);
        assertEquals(fen, position.getFen());
    }

    @Test
    void testDefaultPosition() {
        Position position = Position.getDefault();
        Piece piece1 = position.get(Square.fromName("a1"));
        Piece piece2 = position.get(Square.fromName("b8"));
        Piece piece3 = position.get(Square.fromName("f1"));
        Piece piece4 = position.get(Square.fromName("d8"));
        Piece piece5 = position.get(Square.fromName("e1"));

        assertEquals(PieceType.ROOK, piece1.getType());
        assertEquals(Color.WHITE, piece1.getColor());

        assertEquals(PieceType.KNIGHT, piece2.getType());
        assertEquals(Color.BLACK, piece2.getColor());

        assertEquals(PieceType.BISHOP, piece3.getType());
        assertEquals(Color.WHITE, piece3.getColor());

        assertEquals(PieceType.QUEEN, piece4.getType());
        assertEquals(Color.BLACK, piece4.getColor());

        assertEquals(PieceType.KING, piece5.getType());
        assertEquals(Color.WHITE, piece5.getColor());
    }

    @Test
    void testSetPiece() {
        Position position = new Position();
        position.clear();

        position.set(Square.fromName("e4"), Piece.fromSymbol('P'));
        position.set(Square.fromName("d5"), Piece.fromSymbol('n'));

        String expectedFEN = "8/8/8/3n4/4P3/8/8/8 w - - 0 1";

        assertEquals(expectedFEN, position.getFen());
    }

    @Test
    void testGetTurn_whiteTurn() {
        Position position = Position.getDefault();

        assertEquals(position.getTurn(), Color.WHITE);
    }

    @Test
    void testGetTurn_blackTurn() {
        Position position = Position.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");

        assertEquals(position.getTurn(), Color.BLACK);
    }

    @Test
    void testToggleTurn() {
        Position position = Position.getDefault();

        assertEquals(position.getTurn(), Color.WHITE);
        position.toggleTurn();
        assertEquals(position.getTurn(), Color.BLACK);
    }

    @Test
    void testCastlingRight_defaultPosition() {
        Position position = Position.getDefault();
        assertTrue(position.getCastlingRight('K'));
        assertTrue(position.getCastlingRight('Q'));
        assertTrue(position.getCastlingRight('k'));
        assertTrue(position.getCastlingRight('q'));
    }

    @Test
    void testTheoreticalCastlingRight_fromFen() {
        Position position = Position.fromFen("n3k2b/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(position.getTheoreticalCastlingRight('K'));
        assertTrue(position.getTheoreticalCastlingRight('Q'));
        assertFalse(position.getTheoreticalCastlingRight('k'));
        assertFalse(position.getTheoreticalCastlingRight('q'));
    }

    @Test
    void testTheoreticalCastlingRight_setupPosition() {
        Position position = new Position();
        position.clear();
        position.set(Square.fromName("e1"), Piece.fromSymbol('K'));
        position.set(Square.fromName("h1"), Piece.fromSymbol('R'));
        position.set(Square.fromName("a1"), Piece.fromSymbol('B'));
        position.set(Square.fromName("e8"), Piece.fromSymbol('k'));
        position.set(Square.fromName("a8"), Piece.fromSymbol('r'));

        assertTrue(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertFalse(position.getTheoreticalCastlingRight('k'));
        assertTrue(position.getTheoreticalCastlingRight('q'));
    }

    @Test
    void testPieceCounts_defaultPosition() {
        Position position = Position.getDefault();
        Map<PieceType, Integer> allPieces = position.getPieceCounts("wb");
        assertEquals(16, allPieces.get(PieceType.PAWN));
        assertEquals(4, allPieces.get(PieceType.ROOK));
        assertEquals(4, allPieces.get(PieceType.KNIGHT));
        assertEquals(4, allPieces.get(PieceType.BISHOP));
        assertEquals(2, allPieces.get(PieceType.QUEEN));
        assertEquals(2, allPieces.get(PieceType.KING));
    }
}