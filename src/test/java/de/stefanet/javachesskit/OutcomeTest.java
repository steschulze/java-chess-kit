package de.stefanet.javachesskit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.stefanet.javachesskit.core.Color;
import org.junit.jupiter.api.Test;

class OutcomeTest {

	@Test
	void testResult_stalemate() {
		Outcome outcome = new Outcome(Termination.STALEMATE, null);

		assertEquals(Termination.STALEMATE, outcome.getTermination());
		assertNull(outcome.getWinner());

		assertEquals("1/2-1/2", outcome.result());
		assertEquals("Outcome{termination=STALEMATE, winner=null}", outcome.toString());
	}

	@Test
	void testResult_whiteWins() {
		Outcome outcome = new Outcome(Termination.CHECKMATE, Color.WHITE);

		assertEquals(Termination.CHECKMATE, outcome.getTermination());
		assertEquals(Color.WHITE, outcome.getWinner());

		assertEquals("1-0", outcome.result());
		assertEquals("Outcome{termination=CHECKMATE, winner=WHITE}", outcome.toString());
	}

	@Test
	void testResult_blackWins() {
		Outcome outcome = new Outcome(Termination.CHECKMATE, Color.BLACK);

		assertEquals(Termination.CHECKMATE, outcome.getTermination());
		assertEquals(Color.BLACK, outcome.getWinner());

		assertEquals("0-1", outcome.result());
		assertEquals("Outcome{termination=CHECKMATE, winner=BLACK}", outcome.toString());
	}

}