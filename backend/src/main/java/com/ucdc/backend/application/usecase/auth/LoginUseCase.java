package com.ucdc.backend.application.usecase.auth;

import com.ucdc.backend.application.dto.auth.LoginCommand;
import com.ucdc.backend.application.dto.auth.LoginResult;

public interface LoginUseCase {
    LoginResult login(LoginCommand cmd);
}
