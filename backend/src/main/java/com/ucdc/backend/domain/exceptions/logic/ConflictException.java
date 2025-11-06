package com.ucdc.backend.domain.exceptions.logic;

import java.time.OffsetDateTime;

public class ConflictException extends RuntimeException {
    public ConflictException(String message,String attribute) {
        super(String.format(message, attribute));
    }

    public ConflictException(String message, OffsetDateTime timestamp) {
        super(String.format(message, timestamp));
    }
}
