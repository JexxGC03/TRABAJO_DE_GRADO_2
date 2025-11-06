package com.ucdc.backend.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserCommand(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 4, max = 30) String citizenId,
        @NotBlank @Size(min = 4, max = 30) String serviceNumber,
        @Size(max = 30) String phone,
        @NotBlank @Size(min = 8, max = 72) String password,
        boolean admin
) {
}
