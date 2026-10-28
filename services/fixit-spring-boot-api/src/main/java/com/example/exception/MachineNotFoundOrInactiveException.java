package com.example.exception;

public class MachineNotFoundOrInactiveException extends RuntimeException {
    public MachineNotFoundOrInactiveException(String message) {
        super(message);
    }
}
