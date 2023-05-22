package org.example.exception;

public class InvalidGSMessageFormatException extends RuntimeException {
    public InvalidGSMessageFormatException() {
        super();
    }

    public InvalidGSMessageFormatException(String message) {
        super(message);
    }
}
