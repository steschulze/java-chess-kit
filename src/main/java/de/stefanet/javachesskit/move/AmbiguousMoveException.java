package de.stefanet.javachesskit.move;

/**
 * Thrown when the attempted move is ambiguous in the current position.
 */
public class AmbiguousMoveException extends RuntimeException {
    /**
     * Constructs a new AmbiguousMoveException with the specified detail message.
     *
     * @param message the detail message
     */
    public AmbiguousMoveException(String message) {
        super(message);
    }
}
