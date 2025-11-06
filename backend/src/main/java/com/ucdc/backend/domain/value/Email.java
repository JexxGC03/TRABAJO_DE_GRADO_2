package com.ucdc.backend.domain.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern RX = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        Objects.requireNonNull(value, "email is required");
        value = value.trim().toLowerCase();
        if (!RX.matcher(value).matches()) throw new IllegalArgumentException("invalid email: " + value);
    }

    /** Factory principal. */
    public static Email of(String raw) {
        return new Email(raw);
    }

    /** Permite valores opcionales (null o blanco). */
    public static Email fromNullable(String raw) {
        return (raw == null || raw.isBlank()) ? null : new Email(raw);
    }

    @JsonValue
    public String json() { return value; }
    @JsonCreator
    public static Email fromJson(String raw) { return of(raw); }

    @Override public String toString() { return value; }
}
