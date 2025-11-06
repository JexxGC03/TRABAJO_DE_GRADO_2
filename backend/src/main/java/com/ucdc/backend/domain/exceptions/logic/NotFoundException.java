package com.ucdc.backend.domain.exceptions.logic;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String name, UUID id) {
        super(String.format("%s not found with id %s", name, id));
    }

    public NotFoundException(String message) {super(message);}
}
