package de.stefanet.javachesskit.examples;

import de.stefanet.javachesskit.Board;
import de.stefanet.javachesskit.move.Move;

/**
 * Example of the Scholar's Mate.
 */
public class ScholarsMate {

    /**
     * Setup Scholar's Mate and print outcome.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Board board = new Board();
        board.push(Move.fromUci("e2e4"));
        board.push(Move.fromUci("e7e5"));
        board.push(Move.fromUci("d1h5"));
        board.push(Move.fromUci("b8c6"));
        board.push(Move.fromUci("f1c4"));
        board.push(Move.fromUci("g8f6"));
        board.push(Move.fromUci("h5f7"));

        System.out.println(board.outcome());
    }
}
