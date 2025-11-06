package com.ucdc.backend.domain.security;

public interface PasswordPolicy {
    /**
     * Valida la contraseña contra la política vigente.
     * Reglas sugeridas (implementación): longitud mínima, mezcla de tipos, etc.
     */
    void validate(String rawPassword);
}
