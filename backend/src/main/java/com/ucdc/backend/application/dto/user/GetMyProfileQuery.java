package com.ucdc.backend.application.dto.user;

import java.util.UUID;

public record GetMyProfileQuery(
        UUID userId
) {
}
