package com.ucdc.backend.application.usecase.alert;

import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.model.Alert;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface ListAlertsUseCase {
    record Query(UUID meterId,
                 AlertStatus status,
                 AlertType type,
                 Alert.Granularity granularity,
                 YearMonth from,
                 YearMonth to) {}
    List<Alert> handle(Query q);
}
