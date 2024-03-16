package de.stefanet.javachesskit.core;

public class AmbiguousMoveException extends RuntimeException {
	public AmbiguousMoveException(String message) {
		super(message);
	}
}
