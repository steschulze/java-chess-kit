package de.stefanet.javachesskit.board0x88;

import de.stefanet.javachesskit.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveInfoTest {

	@Test
	void testToString() {
		Position position = new Position();
		MoveInfo moveInfo = position.getMoveInfo(Move.fromUCI("g1f3"));

		assertEquals("MoveInfo{move=Move.fromUCI(g1f3), " +
				"movedPiece=Piece.parseSymbol('N'), " +
				"capturedPiece=null, " +
				"san='Nf3', " +
				"isEnpassant=false, " +
				"isKingSideCastle=false, " +
				"isQueenSideCastle=false, " +
				"isCheck=false, " +
				"isCheckmate=false}", moveInfo.toString());
	}

	@Test
	void testEquals() {
		Position position = new Position();
		MoveInfo moveInfo1 = position.getMoveInfo(Move.fromUCI("e2e4"));
		MoveInfo moveInfo2 = position.getMoveInfo(Move.fromUCI("d2d4"));

		assertTrue(moveInfo1.equals(moveInfo1));
		assertNotEquals(moveInfo1, "e2e4");
		assertNotEquals(moveInfo1, null);
		assertNotEquals(moveInfo1, moveInfo2);
	}

	@Test
	void testHashCode() {
		Position position = new Position();
		MoveInfo moveInfo1 = position.getMoveInfo(Move.fromUCI("e2e4"));
		MoveInfo moveInfo2 = position.getMoveInfo(new Move(Square.fromName("e2"), Square.fromName("e4")));
		MoveInfo moveInfo3 = position.getMoveInfo(Move.fromUCI("d2d4"));

		assertEquals(moveInfo1.hashCode(), moveInfo2.hashCode());
		assertNotEquals(moveInfo1.hashCode(), moveInfo3.hashCode());
	}
}