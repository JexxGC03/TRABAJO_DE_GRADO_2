package com.ucdc.backend.infrastructure.persistence.adapter;

import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.domain.repositories.AlertRepository;
import com.ucdc.backend.infrastructure.persistence.mapper.AlertJpaMapper;
import com.ucdc.backend.infrastructure.persistence.repositories.JpaAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class AlertRepositoryAdapter implements AlertRepository {

    private final JpaAlertRepository jpa;
    private final AlertJpaMapper mapper;

    @Override
    public Optional<Alert> findActiveByMeter(UUID meterId) {
        return jpa.findFirstByMeterIdAndStatusOrderByCreatedAtDesc(meterId, AlertStatus.ACTIVE)
                .map(mapper::from);
    }

    @Override
    public Optional<Alert> findById(UUID id) {
        return jpa.findById(id).map(mapper::from);
    }

    @Override
    public Alert save(Alert alert) {
        var saved = jpa.save(mapper.toEntity(alert));
        return mapper.from(saved);
    }

    @Override
    public List<Alert> findRecentByMeter(UUID meterId) {
        return jpa.findTop10ByMeterIdOrderByCreatedAtDesc(meterId)
                .stream().map(mapper::from).toList();
    }

    @Override
    public boolean existsActive(UUID meterId, AlertType type, Alert.Granularity granularity, YearMonth period) {
        return jpa.existsActive(meterId, type.toString(), granularity.toString(), period.toString());
    }

    @Override
    public List<Alert> search(UUID meterId, AlertStatus status, AlertType type, Alert.Granularity granularity, YearMonth from, YearMonth to) {
        var list = jpa.search(
                meterId,
                status != null ? status.name() : null,
                type   != null ? type.name()   : null,
                granularity      != null ? granularity.name()      : null,
                from   != null ? from.toString() : null,
                to     != null ? to.toString()   : null
        );
        return list.stream().map(mapper::toDomain).toList();
    }
}
