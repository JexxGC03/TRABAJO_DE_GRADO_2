package com.ucdc.backend.domain.security;

import java.util.UUID;

public interface UserSecurityRepository {

    /**
     * Incrementa y devuelve el total de intentos fallidos actuales.
     * Debe ser atómico.
     */
    int incrementFailedAttempts(UUID userId);

    /** Resetea el contador de intentos fallidos a cero. */
    void resetFailedAttempts(UUID userId);

    /** Obtiene el contador actual (0 si no existe registro). */
    int getFailedAttempts(UUID userId);
}
