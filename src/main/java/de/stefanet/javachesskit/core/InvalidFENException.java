package de.stefanet.javachesskit.core;

/**
 * Exception thrown when an invalid FEN (Forsyth&ndash;Edwards Notation) string is encountered.
 */
public class InvalidFENException extends RuntimeException {

	/**
	 * Constructs an InvalidFENException with the specified detail message.
	 *
	 * @param msg The detail message.
	 */
	public InvalidFENException(String msg) {
		super(msg);
	}
}
