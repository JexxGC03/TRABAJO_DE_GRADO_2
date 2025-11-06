package com.ucdc.backend.domain.exceptions.logic;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
