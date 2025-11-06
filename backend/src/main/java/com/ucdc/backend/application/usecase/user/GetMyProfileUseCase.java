package com.ucdc.backend.application.usecase.user;

import com.ucdc.backend.application.dto.user.GetMyProfileQuery;
import com.ucdc.backend.application.dto.user.GetMyProfileResult;

public interface GetMyProfileUseCase {
    GetMyProfileResult handle(GetMyProfileQuery query);
}
