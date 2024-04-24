package de.stefanet.javachesskit;

public class AmbiguousMoveException extends RuntimeException {
    public AmbiguousMoveException(String message) {
        super(message);
    }
}
