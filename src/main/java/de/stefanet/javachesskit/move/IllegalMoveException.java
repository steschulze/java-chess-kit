package de.stefanet.javachesskit.move;

/**
 * Thrown when an illegal move is attempted in the current position.
 */
public class IllegalMoveException extends RuntimeException {

    /**
     * Constructs a new IllegalMoveException with the specified detail message.
     *
     * @param msg the detail message
     */
    public IllegalMoveException(String msg) {
        super(msg);
    }
}
