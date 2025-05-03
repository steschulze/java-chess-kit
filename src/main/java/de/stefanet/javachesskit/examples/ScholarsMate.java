package de.stefanet.javachesskit.examples;

import de.stefanet.javachesskit.Board;

/**
 * Example of the Scholar's Mate.
 */
public class ScholarsMate {

    /**
     * Set up Scholar's Mate and print outcome.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Board board = new Board();

        board.pushSan("e4");

        board.pushSan("e5");
        board.pushSan("Qh5");
        board.pushSan("Nc6");
        board.pushSan("Bc4");
        board.pushSan("Nf6");
        board.pushSan("Qxf7");

        System.out.println(board.outcome());
        System.out.println(board.getFen());
    }
}
