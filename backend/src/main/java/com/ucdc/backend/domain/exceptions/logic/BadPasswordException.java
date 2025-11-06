package com.ucdc.backend.domain.exceptions.logic;

import java.util.List;

public class BadPasswordException extends RuntimeException {
    private final List<String> reasons;

    public BadPasswordException(List<String> reasons) {
        super("Password policy violated: " + String.join("; ", reasons));
        this.reasons = reasons;
    }

    public List<String> getReasons() { return reasons; }
}
