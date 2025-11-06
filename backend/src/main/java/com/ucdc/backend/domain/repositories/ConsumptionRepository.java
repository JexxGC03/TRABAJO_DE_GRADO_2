package com.ucdc.backend.domain.repositories;

import com.ucdc.backend.domain.enums.ConsumptionType;
import com.ucdc.backend.domain.model.Consumption;
import com.ucdc.backend.infrastructure.persistence.entity.ConsumptionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsumptionRepository {

    record DailyPoint(LocalDate date, BigDecimal kwh) {}
    record MonthlyPoint(int month, BigDecimal kwh) {}
    record YearTotal(int year, BigDecimal totalKwh) {}

    // Clave natural: (meterId, type, periodStart)
    Optional<Consumption> findByKey(UUID meterId, ConsumptionType type, OffsetDateTime periodStart);

    // Crea/actualiza por ID (el adapter resuelve el save JPA)
    Consumption save(Consumption c);

    // Consulta genérica por tipo y rango de periodStart
    List<Consumption> findByTypeBetween(UUID meterId, ConsumptionType type, OffsetDateTime from, OffsetDateTime to);
    boolean deleteIfZero(UUID meterId, ConsumptionType type, OffsetDateTime periodStart);
    List<MonthlyPoint> findMonthlyByMeterBetween(UUID meterId, LocalDate from, LocalDate toInclusive);


    List<DailyPoint> findDailyByMeterBetween(UUID meterId, LocalDate from, LocalDate toInclusive);
    BigDecimal sumByMeterBetween(UUID meterId, LocalDate from, LocalDate toInclusive);
    List<MonthlyPoint> findMonthlyByMeterAndYear(UUID meterId, int year);
    BigDecimal sumByMeterAndYear(UUID meterId, int year);
    List<YearTotal>    sumByMeterGroupByYear(UUID meterId, int lastNYears);


    BigDecimal sumMonthlyToDate(UUID meterId, YearMonth ym);;
    /** Histórico de eneros vs eneros… (estacionalidad). 'lastYears' limita cantidad. */
    List<BigDecimal> findMonthlyKwhSameMonthOfYear(UUID meterId, Month month, int lastYears);
    /** kWh del mes inmediatamente anterior a 'ym'. */
    Optional<BigDecimal> findPreviousMonthKwh(UUID meterId, YearMonth ym);

}
