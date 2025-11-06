package com.ucdc.backend.domain.exceptions.logic;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
