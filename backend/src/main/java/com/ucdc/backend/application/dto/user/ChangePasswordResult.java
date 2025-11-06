package com.ucdc.backend.application.dto.user;

import java.time.OffsetDateTime;

public record ChangePasswordResult(
        OffsetDateTime updatedAt
) {
}
