package com.ucdc.backend.infrastructure.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String fullName,   // el front puede componerlo con nombre + apellido
        @Email @NotBlank String email,
        String phone,               // opcional
        String newPassword
) {
}
