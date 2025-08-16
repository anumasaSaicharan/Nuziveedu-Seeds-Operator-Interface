package com.nsl.operatorInterface.exception.exceptionHandler;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
