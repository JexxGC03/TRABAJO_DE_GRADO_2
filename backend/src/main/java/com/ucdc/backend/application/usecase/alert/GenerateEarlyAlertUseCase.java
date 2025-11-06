package com.ucdc.backend.application.usecase.alert;

import com.ucdc.backend.application.dto.alert.GenerateAlertCommand;
import com.ucdc.backend.application.dto.alert.GenerateAlertResult;

public interface GenerateEarlyAlertUseCase {
    GenerateAlertResult handle(GenerateAlertCommand cmd);
}
