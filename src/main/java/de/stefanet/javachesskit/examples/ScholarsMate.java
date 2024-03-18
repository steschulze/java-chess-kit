package de.stefanet.javachesskit.examples;

import de.stefanet.javachesskit.board0x88.Move;
import de.stefanet.javachesskit.board0x88.Position;


public class ScholarsMate {

	public static void main(String[] args) {
		Position position = new Position();
		position.makeMove(Move.fromUCI("e2e4"));
		position.makeMove(Move.fromUCI("e7e5"));
		position.makeMove(Move.fromUCI("d1h5"));
		position.makeMove(Move.fromUCI("b8c6"));
		position.makeMove(Move.fromUCI("f1c4"));
		position.makeMove(Move.fromUCI("g8f6"));
		position.makeMove(Move.fromUCI("h5f7"));

		System.out.println("Checkmate: " + position.isCheckmate());
	}
}
