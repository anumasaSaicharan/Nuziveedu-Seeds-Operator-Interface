package com.nsl.operatorInterface.exception.exceptionHandler;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException (String message) {
        super(message);
    }

}
