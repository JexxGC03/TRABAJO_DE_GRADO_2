package com.ucdc.backend.domain.exceptions.logic;

public class InvalidProviderException extends RuntimeException {
    public InvalidProviderException(String providerName) {
        super("Invalid provider: " + providerName);
    }
}
