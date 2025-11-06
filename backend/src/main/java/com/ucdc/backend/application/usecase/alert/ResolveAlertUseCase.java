package com.ucdc.backend.application.usecase.alert;

import java.util.UUID;

public interface ResolveAlertUseCase {
    void handle(UUID alertId);
}
