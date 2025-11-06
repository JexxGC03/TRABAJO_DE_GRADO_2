package com.ucdc.backend.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutCommand(
        @NotBlank String refreshToken
) {}
