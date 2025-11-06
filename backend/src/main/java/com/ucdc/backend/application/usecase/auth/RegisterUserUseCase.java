package com.ucdc.backend.application.usecase.auth;

import com.ucdc.backend.application.dto.auth.RegisterUserCommand;
import com.ucdc.backend.application.dto.auth.RegisterUserResult;

public interface RegisterUserUseCase {
    RegisterUserResult register(RegisterUserCommand cmd);
}
