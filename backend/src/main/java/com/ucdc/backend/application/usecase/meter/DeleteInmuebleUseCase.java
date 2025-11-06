package com.ucdc.backend.application.usecase.meter;

import java.util.UUID;

public interface DeleteInmuebleUseCase {
    void handle (UUID userId, UUID meterId);
}
