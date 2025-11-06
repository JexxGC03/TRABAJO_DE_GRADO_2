package com.ucdc.backend.application.usecase.auth;

import com.ucdc.backend.application.dto.auth.LogoutCommand;

public interface LogoutUseCase {
    void logout(LogoutCommand cmd);
}
