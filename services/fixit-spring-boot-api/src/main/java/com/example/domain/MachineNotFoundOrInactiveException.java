package com.example.domain;

import java.util.UUID;

public class MachineNotFoundOrInactiveException extends RuntimeException {

    public MachineNotFoundOrInactiveException(UUID machineId) {
        super("Machine not found or inactive: " + machineId);
    }
}
