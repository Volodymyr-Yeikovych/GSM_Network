package org.example.exception;

public class InvalidGsmMessageFormatException extends RuntimeException {
    public InvalidGsmMessageFormatException() {
        super();
    }

    public InvalidGsmMessageFormatException(String message) {
        super(message);
    }
}
