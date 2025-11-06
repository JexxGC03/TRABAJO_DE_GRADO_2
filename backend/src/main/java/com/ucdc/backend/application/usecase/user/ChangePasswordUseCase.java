package com.ucdc.backend.application.usecase.user;

import com.ucdc.backend.application.dto.user.ChangePasswordCommand;
import com.ucdc.backend.application.dto.user.ChangePasswordResult;

public interface ChangePasswordUseCase {
    ChangePasswordResult handle(ChangePasswordCommand command);
}
