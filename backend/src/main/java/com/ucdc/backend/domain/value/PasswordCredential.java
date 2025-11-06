package com.ucdc.backend.domain.value;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record PasswordCredential(
        UUID userId, String passwordHash, OffsetDateTime updatedAt
) {
    public PasswordCredential {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(passwordHash, "passwordHash");
        if (passwordHash.isBlank()) throw new IllegalArgumentException("empty hash");
        updatedAt = (updatedAt != null) ? updatedAt : OffsetDateTime.now();
    }

    /** Para el alta inicial del usuario. */
    public static PasswordCredential forUser(UUID userId, String passwordHash) {
        return new PasswordCredential(userId, passwordHash, OffsetDateTime.now());
    }

    /** Para cambiar/rotar el hash (p.ej., cambio de contraseña). */
    public PasswordCredential rotate(String newHash) {
        return new PasswordCredential(this.userId, newHash, OffsetDateTime.now());
    }

    /** “Touch” cuando quieras solo actualizar timestamp sin cambiar hash (opcional). */
    public PasswordCredential touch() {
        return new PasswordCredential(this.userId, this.passwordHash, OffsetDateTime.now());
    }
}
