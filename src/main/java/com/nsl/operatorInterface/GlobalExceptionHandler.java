package com.nsl.operatorInterface;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.exception.exceptionHandler.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Handle all uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    // 🔹 Handle validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", errors);
    }

    // 🔹 Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    // 🔹 Handle Duplicate Entry
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntry(DuplicateEntryException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate Entry", ex.getMessage());
    }

    // 🔹 Handle Invalid Request
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse> handleInvalidRequest(InvalidRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Request", ex.getMessage());
    }

    // 🔹 Handle Unauthorized
    @ExceptionHandler(UnAuthException.class)
    public ResponseEntity<ApiResponse> handleUnauthorized(UnAuthException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    // 🔹 Handle Unsupported Operation
    @ExceptionHandler(UnsupportedOperation.class)
    public ResponseEntity<ApiResponse> handleUnsupportedOperation(UnsupportedOperation ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Unsupported Operation", ex.getMessage());
    }

    // 🔹 Helper method for consistent responses
    private ResponseEntity<ApiResponse> buildResponse(HttpStatus status, String message, Object data) {
        ApiResponse response = new ApiResponse(status.value(), message, data);
        return ResponseEntity.status(status).body(response);
    }
}
