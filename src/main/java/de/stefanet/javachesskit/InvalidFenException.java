package de.stefanet.javachesskit;

public class InvalidFenException extends RuntimeException {
    public InvalidFenException(String message) {
        super(message);
    }
}
