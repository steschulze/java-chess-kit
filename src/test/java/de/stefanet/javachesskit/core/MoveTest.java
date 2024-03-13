package de.stefanet.javachesskit.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    @Test
    void testGetUciMove_promotion() {
        Move move = new Move(Square.fromName("d7"), Square.fromName("e8"), PieceType.KNIGHT);
        assertEquals("d7e8n", move.getUciMove());
    }

    @Test
    void testMoveCreation_wrongPromotion() {
        assertThrows(AssertionError.class, () -> {
            new Move(Square.fromName("e4"), Square.fromName("e5"), PieceType.KNIGHT);
        });

        assertThrows(AssertionError.class, () -> {
            new Move(Square.fromName("e7"), Square.fromName("e8"), PieceType.PAWN);
        });
    }

    @Test
    void testEquals() {
        Move move1 = Move.fromUCI("e2e4");
        Move move2 = new Move(Square.fromRankAndFile(2, 'e'), Square.fromRankAndFile(4, 'e'));
        assertEquals(move1, move1);
        assertEquals(move1, move2);
        assertFalse(move1.equals("e2e4"));
        assertFalse(move1.equals(null));
    }

    @Test
    void testFromUCI_bigPawnMove() {
        Move move = Move.fromUCI("e2e4");
        assertEquals("e2", move.getSource().getName());
        assertEquals("e4", move.getTarget().getName());
        assertNull(move.getPromotion());
    }

    @Test
    void testFromUCI_promotion() {
        Move move = Move.fromUCI("d7d8q");
        assertEquals("d7", move.getSource().getName());
        assertEquals("d8", move.getTarget().getName());
        assertEquals(PieceType.QUEEN, move.getPromotion());
    }

    @Test
    void testFromUCI_wrongFormat() {
        assertThrows(IllegalArgumentException.class, () -> Move.fromUCI("d7d8Q"));
    }

    @Test
    void testToString() {
        Move move = new Move(Square.fromRankAndFile(2, 'a'), Square.fromRankAndFile(4, 'a'));
        assertEquals("Move.fromUCI(a2a4)", move.toString());
    }

    @Test
    void testHashCode() {
        Move move1 = new Move(Square.fromName("e7"), Square.fromName("e8"));
        Move move2 = Move.fromUCI("e7e8");
        Move move3 = Move.fromUCI("e7e8r");

        assertEquals(move1.hashCode(), move2.hashCode());
        assertNotEquals(move1.hashCode(), move3.hashCode());
    }
}