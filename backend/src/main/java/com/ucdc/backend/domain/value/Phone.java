package com.ucdc.backend.domain.value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;
import java.util.regex.Pattern;

public record Phone(String value) {

    // Permite dígitos, +, -, (), espacios. Longitud de 5 a 32 caracteres
    private static final Pattern ALLOWED = Pattern.compile("[0-9+\\-()\\s]{5,32}");

    // Canonical constructor: normaliza y valida
    public Phone {
        Objects.requireNonNull(value, "phone is required");
        value = value.trim();
        if (value.isEmpty()) throw new IllegalArgumentException("phone cannot be blank");
        if (value.length() > 32) throw new IllegalArgumentException("phone too long");
        if (!ALLOWED.matcher(value).matches()) {
            throw new IllegalArgumentException("invalid phone format: " + value);
        }
    }

    /** Factory estándar para usar desde servicios/mappers. */
    public static Phone of(String raw) {
        return new Phone(raw);
    }

    /** Útil cuando el campo es opcional (p.ej., en registro). */
    public static Phone fromNullable(String raw) {
        return (raw == null || raw.isBlank()) ? null : new Phone(raw);
    }

    /** Devuelve el número sin separadores (manteniendo + si fue provisto). */
    public String normalized() {
        // conserva el '+' inicial si existe y remueve lo demás no-dígito
        if (value.startsWith("+")) {
            return "+" + value.substring(1).replaceAll("\\D", "");
        }
        return value.replaceAll("\\D", "");
    }

    /** Representación E.164 usando un prefijo por defecto si no vino con +. */
    public String e164(String defaultCountryCode) {
        String n = normalized();
        if (n.startsWith("+")) return n;
        if (defaultCountryCode == null || defaultCountryCode.isBlank()) {
            throw new IllegalArgumentException("defaultCountryCode required for non-E.164 phones");
        }
        String cc = defaultCountryCode.startsWith("+") ? defaultCountryCode : "+" + defaultCountryCode;
        return cc + n;
    }

    /** Enmascara para logs/UI: ****1234 (últimos 4). */
    public String masked() {
        String digits = value.replaceAll("\\D", "");
        if (digits.length() <= 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "****" + last4;
    }

    // Jackson
    @JsonValue
    public String json() { return value; }
    @JsonCreator
    public static Phone fromJson(String raw) { return of(raw); }

    @Override public String toString() { return value; }
}
