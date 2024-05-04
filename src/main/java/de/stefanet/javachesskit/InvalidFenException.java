package de.stefanet.javachesskit;

/**
 * Thrown when the FEN is syntactically invalid.
 */
public class InvalidFenException extends RuntimeException {
    /**
     * Constructs a new InvalidFenException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidFenException(String message) {
        super(message);
    }
}
