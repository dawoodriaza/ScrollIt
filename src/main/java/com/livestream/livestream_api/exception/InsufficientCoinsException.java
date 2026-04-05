package com.livestream.livestream_api.exception;



public class InsufficientCoinsException extends RuntimeException {
    public InsufficientCoinsException(String message) {

        super(message);

    }
}