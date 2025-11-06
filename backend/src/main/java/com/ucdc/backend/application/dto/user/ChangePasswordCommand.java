package com.ucdc.backend.application.dto.user;

import java.util.UUID;

public record ChangePasswordCommand(
        UUID userId,
        String currentPassword,
        String newPassword,
        String confirmNewPassword
) {
}
