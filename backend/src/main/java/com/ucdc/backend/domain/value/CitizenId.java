package com.ucdc.backend.domain.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public record CitizenId(String value) {

    public CitizenId {
        Objects.requireNonNull(value, "citizenId is required");
        value = value.trim();
        if (value.isBlank()) throw new IllegalArgumentException("citizenId cannot be blank");
        if (value.length() > 12) throw new IllegalArgumentException("citizenId too long");
    }

    public static CitizenId of(String raw) {
        return new CitizenId(raw);
    }

    public static CitizenId fromNullable(String raw) {
        return (raw == null || raw.isBlank()) ? null : new CitizenId(raw);
    }

    @JsonValue
    public String json() { return value; }
    @JsonCreator
    public static CitizenId fromJson(String raw) { return of(raw); }

    @Override public String toString() { return value; }
}
