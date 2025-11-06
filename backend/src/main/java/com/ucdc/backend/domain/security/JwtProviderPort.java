package com.ucdc.backend.domain.security;

import com.ucdc.backend.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de dominio para generación y validación de JWT.
 * Define las operaciones necesarias sin acoplar a librerías externas.
 */
public interface JwtProviderPort {

    /**
     * Genera un token de acceso corto (normalmente 1h).
     */
    String generateAccessToken(User user, long ttlSeconds);

    /**
     * Genera un refresh token de mayor duración (días o semanas).
     */
    String generateRefreshToken(User user, long ttlSeconds);

    /**
     * Extrae el ID de usuario desde un token de acceso válido.
     * Devuelve Optional.empty() si no es válido o expiró.
     */
    Optional<UUID> parseUserIdFromAccess(String jwt);
}
