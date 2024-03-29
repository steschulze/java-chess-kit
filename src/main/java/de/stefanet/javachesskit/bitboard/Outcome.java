package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.Color;

public class Outcome {
	private Termination termination;
	private Color winner;

	public Outcome(Termination termination, Color winner) {
		this.termination = termination;
		this.winner = winner;
	}

	public String result() {
		return winner == null ? "1/2-1/2" : winner == Color.WHITE ? "1-0" : "0-1";
	}

	public Termination getTermination() {
		return termination;
	}

	public Color getWinner() {
		return winner;
	}
}
