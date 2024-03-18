package de.stefanet.javachesskit;

/**
 * Exception thrown when an invalid FEN (Forsyth&ndash;Edwards Notation) string is encountered.
 */
public class InvalidMoveException extends RuntimeException {

	/**
	 * Constructs an InvalidFENException with the specified detail message.
	 *
	 * @param msg The detail message.
	 */
	public InvalidMoveException(String msg) {
		super(msg);
	}
}
