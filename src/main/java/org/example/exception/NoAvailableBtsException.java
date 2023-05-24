package org.example.exception;

public class NoAvailableBtsException extends RuntimeException{
    public NoAvailableBtsException(String message) {
        super(message);
    }

    public NoAvailableBtsException() {
        super();
    }
}
