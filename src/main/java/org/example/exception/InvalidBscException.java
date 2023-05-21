package org.example.exception;

public class InvalidBscException extends RuntimeException{
    public InvalidBscException() {
        super();
    }

    public InvalidBscException(String message) {
        super(message);
    }
}
