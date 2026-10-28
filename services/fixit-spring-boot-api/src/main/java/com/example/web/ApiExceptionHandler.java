package com.example.web;

import com.example.contract.ErrorBody;
import com.example.exception.MachineNotFoundOrInactiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> onValidationError(MethodArgumentNotValidException e) {
        List<Map<String, Object>> details = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.<String, Object>of("field", fe.getField(), "message", fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorBody("VALIDATION_ERROR", details, "Request validation failed", UUID.randomUUID()));
    }

    @ExceptionHandler(MachineNotFoundOrInactiveException.class)
    public ResponseEntity<ErrorBody> onMachineNotFound(MachineNotFoundOrInactiveException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorBody("MACHINE_NOT_FOUND_OR_INACTIVE", null, e.getMessage(), UUID.randomUUID()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> onError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorBody("internal_error", null, e.getMessage() == null ? "error" : e.getMessage(), UUID.randomUUID()));
    }
}
