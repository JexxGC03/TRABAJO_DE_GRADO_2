package com.ucdc.backend.application.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateProfileCommand(
        @NotNull UUID userId,
        @Size(max = 120) String name,
        @Size(max = 120) String email,
        @Size(max = 30) String phone,
        @Size(min = 8, max = 72) String newPassword
) {
}
