package de.stefanet.javachesskit.board0x88;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void testSymbolParsing() {
        Piece blackQueen = new Piece('q');
        assertEquals('q', blackQueen.getSymbol());

        Piece whitePawn = new Piece('P');
        assertEquals('P', whitePawn.getSymbol());
    }

    @Test
    void testSymbolParsing_wrongSymbol() {
        assertThrows(IllegalArgumentException.class, () -> new Piece('e'));
    }

    @Test
    void testEquality() {
        Piece whiteBishop1 = Piece.fromTypeAndColor(PieceType.BISHOP, Color.WHITE);
        Piece blackKing = Piece.fromTypeAndColor(PieceType.KING, Color.BLACK);
        Piece whiteKing = Piece.fromTypeAndColor(PieceType.KING, Color.WHITE);
        Piece whiteBishop2 = Piece.fromTypeAndColor(PieceType.BISHOP, Color.WHITE);

        assertEquals(whiteBishop1, whiteBishop1);
        assertEquals(whiteBishop1, whiteBishop2);
        assertNotEquals(whiteKing, blackKing);
        assertNotEquals(whiteKing, whiteBishop1);
        assertNotEquals(null, blackKing);
        assertNotEquals("K", whiteKing);
    }

    @Test
    void testSimpleProperties() {
        Piece whiteKnight = Piece.fromTypeAndColor(PieceType.KNIGHT, Color.WHITE);

        assertEquals(Color.WHITE, whiteKnight.getColor());
        assertEquals(PieceType.KNIGHT, whiteKnight.getType());
    }

    @Test
    void testToString() {
        Piece whiteRook = Piece.fromTypeAndColor(PieceType.ROOK, Color.WHITE);
        assertEquals("Piece.parseSymbol('R')", whiteRook.toString());
    }

    @Test
    void testHashCode() {
        Piece whitePawn1 = Piece.fromTypeAndColor(PieceType.PAWN, Color.WHITE);
        Piece whitePawn2 = Piece.fromTypeAndColor(PieceType.PAWN, Color.WHITE);
        Piece blackPawn = Piece.fromTypeAndColor(PieceType.PAWN, Color.BLACK);

        assertEquals(whitePawn1.hashCode(), whitePawn2.hashCode());
        assertNotEquals(whitePawn1.hashCode(), blackPawn.hashCode());
    }

}