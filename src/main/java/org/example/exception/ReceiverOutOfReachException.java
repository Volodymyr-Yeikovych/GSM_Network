package org.example.exception;
public class ReceiverOutOfReachException extends RuntimeException {
    public ReceiverOutOfReachException(String message) {
        super(message);
    }

    public ReceiverOutOfReachException() {
        super();
    }
}
