package com.ucdc.backend.application.usecase.user;

import com.ucdc.backend.application.dto.user.UpdateProfileCommand;
import com.ucdc.backend.application.dto.user.UpdateProfileResult;

public interface UpdateProfileUseCase {
    UpdateProfileResult update(UpdateProfileCommand cmd);
}
