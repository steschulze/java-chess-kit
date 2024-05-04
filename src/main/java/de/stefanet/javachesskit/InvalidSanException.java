package de.stefanet.javachesskit;

/**
 * Thrown when the standard algebraic notation (SAN) is invalid.
 */
public class InvalidSanException extends RuntimeException {

    /**
     * Constructs a new InvalidSanException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidSanException(String message) {
        super(message);
    }

}
