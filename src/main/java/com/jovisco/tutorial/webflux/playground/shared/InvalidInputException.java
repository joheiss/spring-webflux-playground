package com.jovisco.tutorial.webflux.playground.shared;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
