package com.ucdc.backend.domain.security;

public interface PasswordEncoder {
    /** Devuelve el hash seguro de la contraseña en texto plano. */
    String encode(String rawPassword);

    /** Verifica si raw coincide con el hash almacenado. */
    boolean matches(String rawPassword, String encodedHash);
}
