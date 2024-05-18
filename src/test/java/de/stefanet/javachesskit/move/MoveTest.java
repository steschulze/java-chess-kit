package de.stefanet.javachesskit.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.stefanet.javachesskit.core.PieceType;
import de.stefanet.javachesskit.core.Square;
import org.junit.jupiter.api.Test;

class MoveTest {

    @Test
    void testGetUciMove_promotion() {
        Move move = new Move(Square.parseSquare("d7"), Square.parseSquare("e8"), PieceType.KNIGHT);
        assertEquals("d7e8n", move.getUciMove());
    }

    @Test
    void testMoveCreation_wrongPromotion() {
        assertThrows(IllegalArgumentException.class, () -> new Move(Square.E4, Square.E5, PieceType.KNIGHT));

        assertThrows(IllegalArgumentException.class, () -> new Move(Square.E7, Square.E8, PieceType.PAWN));
    }

    @Test
    void testEquals() {
        Move move1 = Move.fromUci("e2e4");
        Move move2 = new Move(Square.getSquare('e', 2), Square.getSquare('e', 4));
        assertEquals(move1, move1);
        assertEquals(move1, move2);
        assertNotEquals("e2e4", move1);
        assertNotEquals(null, move1);
    }

    @Test
    void testFromUci_bigPawnMove() {
        Move move = Move.fromUci("e2e4");
        assertEquals("e2", move.getSource().getName());
        assertEquals("e4", move.getTarget().getName());
        assertNull(move.getPromotion());
    }

    @Test
    void testFromUci_promotion() {
        Move move = Move.fromUci("d7d8q");
        assertEquals("d7", move.getSource().getName());
        assertEquals("d8", move.getTarget().getName());
        assertEquals(PieceType.QUEEN, move.getPromotion());
    }

    @Test
    void testFromUci_wrongFormat() {
        assertThrows(IllegalArgumentException.class, () -> Move.fromUci("d7d8Q"));
    }

    @Test
    void testToString() {
        Move move = new Move(Square.getSquare('a', 2), Square.getSquare('a', 4));
        assertEquals("Move.fromUCI(a2a4)", move.toString());
    }

    @Test
    void testHashCode() {
        Move move1 = new Move(Square.parseSquare("e7"), Square.parseSquare("e8"));
        Move move2 = Move.fromUci("e7e8");
        Move move3 = Move.fromUci("e7e8r");

        assertEquals(move1.hashCode(), move2.hashCode());
        assertNotEquals(move1.hashCode(), move3.hashCode());
    }
}