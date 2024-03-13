package de.stefanet.javachesskit.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void testSymbolParsing() {
        Piece blackQueen = Piece.fromSymbol('q');
        assertEquals('q', blackQueen.getSymbol());

        Piece whitePawn = Piece.fromSymbol('P');
        assertEquals('P', whitePawn.getSymbol());
    }

    @Test
    void testSymbolParsing_wrongSymbol() {
        assertThrows(IllegalArgumentException.class, () -> Piece.fromSymbol('e'));
    }

    @Test
    void testEquality() {
        Piece whiteBishop1 = new Piece(PieceType.BISHOP, Color.WHITE);
        Piece blackKing = new Piece(PieceType.KING, Color.BLACK);
        Piece whiteKing = new Piece(PieceType.KING, Color.WHITE);
        Piece whiteBishop2 = new Piece(PieceType.BISHOP, Color.WHITE);

        assertEquals(whiteBishop1, whiteBishop1);
        assertEquals(whiteBishop1, whiteBishop2);
        assertNotEquals(whiteKing, blackKing);
        assertNotEquals(whiteKing, whiteBishop1);
        assertFalse(blackKing.equals(null));
        assertFalse(whiteKing.equals("K"));
    }

    @Test
    void testSimpleProperties() {
        Piece whiteKnight = new Piece(PieceType.KNIGHT, Color.WHITE);

        assertEquals(Color.WHITE, whiteKnight.getColor());
        assertEquals(PieceType.KNIGHT, whiteKnight.getType());
    }

    @Test
    void testToString() {
        Piece whiteRook = new Piece(PieceType.ROOK, Color.WHITE);
        assertEquals("Piece.parseSymbol('R')", whiteRook.toString());
    }

    @Test
    void testHashCode() {
        Piece whitePawn1 = new Piece(PieceType.PAWN, Color.WHITE);
        Piece whitePawn2 = new Piece(PieceType.PAWN, Color.WHITE);
        Piece blackPawn = new Piece(PieceType.PAWN, Color.BLACK);

        assertEquals(whitePawn1.hashCode(), whitePawn2.hashCode());
        assertNotEquals(whitePawn1.hashCode(), blackPawn.hashCode());
    }

}