package com.ucdc.backend.application.dto.user;

import java.util.UUID;

public record UpdateProfileResult(
        String name,
        String email,
        String phone
) {
}
