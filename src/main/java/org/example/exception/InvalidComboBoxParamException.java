package org.example.exception;

public class InvalidComboBoxParamException extends RuntimeException {
    public InvalidComboBoxParamException(String msg) {
        super(msg);
    }

    public InvalidComboBoxParamException() {
        super();
    }
}
