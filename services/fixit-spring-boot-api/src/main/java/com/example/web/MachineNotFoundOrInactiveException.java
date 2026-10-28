package com.example.web;

public class MachineNotFoundOrInactiveException extends RuntimeException {
    public MachineNotFoundOrInactiveException(String machineId) {
        super("Machine not found or inactive: " + machineId);
    }
}
