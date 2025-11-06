package com.ucdc.backend.application.usecase.auth;

import com.ucdc.backend.application.dto.auth.LoginResult;
import com.ucdc.backend.application.dto.auth.RefreshTokenCommand;

public interface RefreshTokenUseCase {
    LoginResult refresh(RefreshTokenCommand cmd);
}
