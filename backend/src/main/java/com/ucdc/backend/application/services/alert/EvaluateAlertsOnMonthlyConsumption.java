package com.ucdc.backend.application.services.alert;

import com.ucdc.backend.application.events.MonthlyConsumptionRecorded;
import com.ucdc.backend.domain.enums.AlertType;
import com.ucdc.backend.domain.model.Alert;
import com.ucdc.backend.domain.model.MeterQuota;
import com.ucdc.backend.domain.repositories.AlertRepository;
import com.ucdc.backend.domain.repositories.ConsumptionRepository;
import com.ucdc.backend.domain.repositories.MeterQuotaRepository;
import com.ucdc.backend.domain.services.StatisticalAlertPolicy;
import com.ucdc.backend.domain.services.detectors.Finding;
import com.ucdc.backend.domain.services.detectors.QuotaDetector;
import com.ucdc.backend.domain.services.detectors.SpikeDetector;
import com.ucdc.backend.domain.services.detectors.ZScoreDetector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(EvaluateAlertsOnMonthlyConsumption.AlertsProps.class)
public class EvaluateAlertsOnMonthlyConsumption {

    private final ConsumptionRepository consumptionRepo;
    private final MeterQuotaRepository quotaRepo;
    private final AlertRepository alertRepo;
    private final AlertsProps props;


    @Getter
    @Setter
    @ConfigurationProperties(prefix = "alerts")
    public static class AlertsProps {
        /**
         * Ej. 2
         */
        private BigDecimal kSigma = BigDecimal.valueOf(2);
        /**
         * Ej. 0.30 = 30%
         */
        private BigDecimal spikePct = new BigDecimal("0.30");
        /**
         * Mínimo muestras para estadística. Ej. 12
         */
        private int minSamples = 12;
        /**
         * Años a mirar para mes homólogo. Ej. 6
         */
        private int sameMonthYears = 6;
        /**
         * Toggle general
         */
        private boolean enabled = true;
    }

    @EventListener
    @Transactional
    public void on(MonthlyConsumptionRecorded e) {
        if (!props.enabled) return;

        UUID meterId = e.meterId();
        YearMonth ym = e.period();
        BigDecimal currentMonth = e.kwh(); // viene del bucket MONTHLY

        // 0) Deduplicación por tipo/periodo (si ya existe alerta activa idéntica, no crear)
        if (existsActive(meterId, AlertType.STATISTICAL_ANOMALY, ym) ||
                existsActive(meterId, AlertType.SPIKE, ym) ||
                existsActive(meterId, AlertType.QUOTA_OVERUSE, ym)) {
            // seguimos evaluando por si un tipo NO existe aún, pero evita duplicar “igual tipo”
        }

        // 1) CUOTA (si hay cuota activa mensual)
        OffsetDateTime now = OffsetDateTime.now();
        quotaRepo.findActiveByMeter(meterId, now)
                .flatMap(q -> checkQuotaMonthly(currentMonth, q))
                .ifPresent(f -> persistAlert(meterId, ym, f));

        // 2) ESTADÍSTICA (mes homólogo años previos)
        var hist = findHomologousHistory(meterId, ym);
        if (hist.size() >= props.minSamples) {
            var stats = StatisticalAlertPolicy.statsOf(hist);
            ZScoreDetector.check(currentMonth, stats, props.kSigma)
                    .ifPresent(f -> persistAlert(meterId, ym, f));
        }

        // 3) SPIKE (% vs mes anterior)
        consumptionRepo.findPreviousMonthKwh(meterId, ym)
                .flatMap(prev -> SpikeDetector.check(currentMonth, prev, props.spikePct))
                .ifPresent(f -> persistAlert(meterId, ym, f));
    }

    private Optional<Finding> checkQuotaMonthly(BigDecimal monthKwh, MeterQuota q) {
        return QuotaDetector.checkMonthly(monthKwh, q);
    }

    private List<BigDecimal> findHomologousHistory(UUID meterId, YearMonth ym) {
        return consumptionRepo.findMonthlyKwhSameMonthOfYear(
                meterId, ym.getMonth(), props.sameMonthYears
        );
    }

    private boolean existsActive(UUID meterId, AlertType type, YearMonth ym) {
        return alertRepo.existsActive(meterId, type, Alert.Granularity.MONTHLY, ym);
    }

    private void persistAlert(UUID meterId, YearMonth ym, Finding f) {
        // Evita duplicados por tipo/periodo
        if (alertRepo.existsActive(meterId, f.type(), Alert.Granularity.MONTHLY, ym)) return;

        var alert = Alert.create(
                UUID.randomUUID(), meterId,
                f.expectedOrThreshold(), f.observed(),
                f.type(), f.reason(),
                Alert.Granularity.MONTHLY, ym
        );
        // status ACTIVE por defecto en fábrica
        alertRepo.save(alert);
    }
}
