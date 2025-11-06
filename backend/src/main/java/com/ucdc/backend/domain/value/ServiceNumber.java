package com.ucdc.backend.domain.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public record ServiceNumber(String value) {

    public ServiceNumber {
        Objects.requireNonNull(value, "serviceNumber is required");
        value = value.trim();
        if (value.isBlank()) throw new IllegalArgumentException("serviceNumber cannot be blank");
        if (value.length() > 64) throw new IllegalArgumentException("serviceNumber too long");
    }

    public static ServiceNumber of(String raw) {
        return new ServiceNumber(raw);
    }

    public static ServiceNumber fromNullable(String raw) {
        return (raw == null || raw.isBlank()) ? null : new ServiceNumber(raw);
    }

    @JsonValue
    public String json() { return value; }
    @JsonCreator
    public static ServiceNumber fromJson(String raw) { return of(raw); }

    @Override public String toString() { return value; }
}
