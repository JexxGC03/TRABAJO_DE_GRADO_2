package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.model.Alert;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertRepository {

    Optional<Alert> findActiveByMeter(UUID meterId);
    Optional<Alert> findById(UUID id);
    Alert save(Alert alert);
    List<Alert> findRecentByMeter(UUID meterId);
    boolean existsActive(UUID meterId, AlertType type, Alert.Granularity granularity, YearMonth period);
    List<Alert> search(UUID meterId, AlertStatus status, AlertType type, Alert.Granularity granularity, YearMonth from, YearMonth to);
}
