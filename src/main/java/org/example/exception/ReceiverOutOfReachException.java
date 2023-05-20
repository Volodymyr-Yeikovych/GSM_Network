package org.example.exception;

import org.example.model.Receiver;

import java.util.function.Supplier;

public class ReceiverOutOfReachException extends RuntimeException {
    public ReceiverOutOfReachException(String message) {
        super(message);
    }

    public ReceiverOutOfReachException() {
        super();
    }
}
