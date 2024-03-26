package de.stefanet.javachesskit;

public class IllegalMoveException extends RuntimeException {

	public IllegalMoveException(String msg) {
		super(msg);
	}
}
