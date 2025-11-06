package com.ucdc.backend.application.dto.alert;

import java.util.UUID;

public record GenerateAlertResult(
        UUID alertId,
        String status,
        String message
) {
}
