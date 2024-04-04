package de.stefanet.javachesskit.examples;

import de.stefanet.javachesskit.Move;
import de.stefanet.javachesskit.bitboard.Board;


public class ScholarsMate {

	public static void main(String[] args) {
		Board board = new Board();
		board.push(Move.fromUCI("e2e4"));
		board.push(Move.fromUCI("e7e5"));
		board.push(Move.fromUCI("d1h5"));
		board.push(Move.fromUCI("b8c6"));
		board.push(Move.fromUCI("f1c4"));
		board.push(Move.fromUCI("g8f6"));
		board.push(Move.fromUCI("h5f7"));

		System.out.println(board.outcome());
	}
}
