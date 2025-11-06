package com.ucdc.backend.infrastructure.web.dto.user;

import lombok.Builder;

@Builder
public record ProfileResponse(
        String fullName,
        String email,
        String phone
) {
}
