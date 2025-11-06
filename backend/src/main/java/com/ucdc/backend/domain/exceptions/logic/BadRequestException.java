package com.ucdc.backend.domain.exceptions.logic;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
