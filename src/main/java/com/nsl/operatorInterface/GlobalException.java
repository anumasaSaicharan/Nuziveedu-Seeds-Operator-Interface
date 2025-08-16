package com.nsl.operatorInterface;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.exception.exceptionHandler.DuplicateEntryException;
import com.nsl.operatorInterface.exception.exceptionHandler.InvalidRequestException;
import com.nsl.operatorInterface.exception.exceptionHandler.NoReferralFoundException;
import com.nsl.operatorInterface.exception.exceptionHandler.ResourceNotFoundException;
import com.nsl.operatorInterface.exception.exceptionHandler.UnAuthException;
import com.nsl.operatorInterface.exception.exceptionHandler.UnsupportedOperation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    // Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failure", ex.getMessage());
    }

    // Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return buildResponse(HttpStatus.BAD_REQUEST, "validation error", errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "failure", ex.getMessage());
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntry(DuplicateEntryException ex) {
        return buildResponse(HttpStatus.CONFLICT, "failure", ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse> handleInvalidRequest(InvalidRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "failure", ex.getMessage());
    }

    @ExceptionHandler(UnAuthException.class)
    public ResponseEntity<ApiResponse> handleUnauthorized(UnAuthException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "failure", ex.getMessage());
    }

    @ExceptionHandler(NoReferralFoundException.class)
    public ResponseEntity<ApiResponse> handleNoReferralFound(NoReferralFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "failure", ex.getMessage());
    }

    @ExceptionHandler(UnsupportedOperation.class)
    public ResponseEntity<ApiResponse> handleUnsupportedOperation(UnsupportedOperation ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "failure", ex.getMessage());
    }

    // Helper Method
    private ResponseEntity<ApiResponse> buildResponse(HttpStatus status, String message, Object body) {
        return ResponseEntity.status(status).body(new ApiResponse(status.value(), message, body));
    }
}
