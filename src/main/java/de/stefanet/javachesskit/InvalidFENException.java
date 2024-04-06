package de.stefanet.javachesskit;

public class InvalidFENException extends RuntimeException {
	public InvalidFENException(String message) {
		super(message);
	}
}
