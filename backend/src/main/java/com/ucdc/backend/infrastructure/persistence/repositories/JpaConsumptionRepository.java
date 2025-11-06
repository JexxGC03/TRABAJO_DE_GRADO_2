package com.ucdc.backend.infrastructure.persistence.repositories;

import com.ucdc.backend.domain.enums.ConsumptionType;
import com.ucdc.backend.infrastructure.persistence.entity.ConsumptionEntity;
import com.ucdc.backend.infrastructure.persistence.projections.DailyPointProjection;
import com.ucdc.backend.infrastructure.persistence.projections.MonthlyPointProjection;
import com.ucdc.backend.infrastructure.persistence.projections.MonthlyPointWithYearProjection;
import com.ucdc.backend.infrastructure.persistence.projections.YearAggregateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaConsumptionRepository extends JpaRepository<ConsumptionEntity, UUID> {

    // Clave natural
    Optional<ConsumptionEntity> findByMeter_IdAndConsumptionTypeAndPeriodStart(
            UUID meterId, ConsumptionType consumptionType, OffsetDateTime periodStart);

    // Por tipo y rango de ventana
    List<ConsumptionEntity> findByMeter_IdAndConsumptionTypeAndPeriodStartBetweenOrderByPeriodStartAsc(
            UUID meterId, ConsumptionType type, OffsetDateTime from, OffsetDateTime to);

    // Suma mensual (filtra type = MONTHLY y por año/mes de periodStart)
    @Query("""
           SELECT COALESCE(SUM(c.kwh), 0)
           FROM ConsumptionEntity c
           WHERE c.meter.id = :meterId
             AND c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.MONTHLY
             AND FUNCTION('YEAR', c.periodStart) = :year
             AND FUNCTION('MONTH', c.periodStart) = :month
           """)
    BigDecimal sumMonthlyByMeter(UUID meterId, int year, int month);

    // Diario entre fechas: type = DAILY y periodStart en rango (adapter hará el toOffsetDateTime)
    @Query("""
           SELECT c
           FROM ConsumptionEntity c
           WHERE c.meter.id = :meterId
             AND c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.DAILY
             AND c.periodStart BETWEEN :fromTs AND :toTs
           ORDER BY c.periodStart ASC
           """)
    List<ConsumptionEntity> findDailyBetween(UUID meterId, OffsetDateTime fromTs, OffsetDateTime toTs);

    // Mensual por año: type = MONTHLY y year(periodStart) = :year
    @Query("""
           SELECT c
           FROM ConsumptionEntity c
           WHERE c.meter.id = :meterId
             AND c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.MONTHLY
             AND FUNCTION('YEAR', c.periodStart) = :year
           ORDER BY c.periodStart ASC
           """)
    List<ConsumptionEntity> findMonthlyInYear(UUID meterId, int year);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           delete from ConsumptionEntity c
           where c.meter.id = :meterId
             and c.consumptionType = :type
             and c.periodStart = :start
             and c.kwh = 0
           """)
    int deleteByKeyIfZero(@Param("meterId") UUID meterId, @Param("type") ConsumptionType type, @Param("start") OffsetDateTime start);

    // DAILY
    @Query("""
        select c.periodStart as date, c.kwh as kwh
        from ConsumptionEntity c
        where c.meter.id = :meterId
            and c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.DAILY
            and c.periodStart >= :fromStart
            and c.periodStart <  :toExclusive
        order by c.periodStart asc
    """)
    List<DailyPointProjection> findDailyRowsBetween(UUID meterId, OffsetDateTime fromStart, OffsetDateTime toExclusive);

    // MONTHLY (por año)
    @Query("""
        select function('YEAR', c.periodStart) as year,
        function('MONTH', c.periodStart) as month,
         c.kwh as kwh
    from ConsumptionEntity c
    where c.meter.id = :meterId
        and c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.MONTHLY
        and function('YEAR', c.periodStart) = :year
    order by function('MONTH', c.periodStart) asc
    """)
    List<MonthlyPointWithYearProjection> findMonthlyRowsByYear(UUID meterId, int year);

    // SUM MONTHLY por año
    @Query("""
        select coalesce(sum(c.kwh), 0)
        from ConsumptionEntity c
        where c.meter.id = :meterId
            and c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.MONTHLY
            and function('YEAR', c.periodStart) = :year
    """)
    BigDecimal sumMonthlyByYear(UUID meterId, int year);

    // Agrupado por año (últimos N)
    @Query("""
        select function('YEAR', c.periodStart) as year,
            coalesce(sum(c.kwh), 0) as totalKwh
        from ConsumptionEntity c
        where c.meter.id = :meterId
            and c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.MONTHLY
            and function('YEAR', c.periodStart) >= :minYear
        group by function('YEAR', c.periodStart)
        order by function('YEAR', c.periodStart) desc
    """)
    List<YearAggregateProjection> sumMonthlyGroupByYearFrom(UUID meterId, int minYear);

    @Query("""
    select coalesce(sum(c.kwh), 0)
    from ConsumptionEntity c
    where c.meter.id = :meterId
      and c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.DAILY
      and c.periodStart >= :fromStart
      and c.periodStart <  :toExclusive
""")
    BigDecimal sumDailyBetween(@Param("meterId") UUID meterId,
                               @Param("fromStart") OffsetDateTime fromStart,
                               @Param("toExclusive") OffsetDateTime toExclusive);


    @Query("""
        select function('MONTH', c.periodStart) as month,
             c.kwh as kwh
        from ConsumptionEntity c
        where c.meter.id = :meterId
            and c.consumptionType = com.ucdc.backend.domain.enums.ConsumptionType.MONTHLY
            and c.periodStart >= :fromStart
            and c.periodStart <  :toExclusive
        order by function('YEAR', c.periodStart), function('MONTH', c.periodStart)
    """)
    List<MonthlyPointProjection> findMonthlyRowsBetween(@Param("meterId") UUID meterId,
                                                        @Param("fromStart") OffsetDateTime fromStart,
                                                        @Param("toExclusive") OffsetDateTime toExclusive);


    // A) sumMonthlyToDate — DAILY: sumar 1..hoy; MONTHLY: devolver kwh del registro del mes
    @Query(value = """
      -- SQL Server
      SELECT COALESCE(SUM(c.kwh), 0)
      FROM consumptions c
      WHERE c.meter_id = :meterId
        AND c.consumption_type = 'DAILY'
        AND c.period_start >= DATEFROMPARTS(:year, :month, 1)
        AND c.period_start <  DATEADD(DAY, 1, CAST(GETDATE() AS date))
      """, nativeQuery = true)
    BigDecimal sumDailyMonthToDate(@Param("meterId") UUID meterId,
                                   @Param("year") int year,
                                   @Param("month") int month);

    @Query(value = """
      SELECT TOP 1 c.kwh
      FROM consumptions c
      WHERE c.meter_id = :meterId
        AND c.consumption_type = 'MONTHLY'
        AND c.period_start >= DATEFROMPARTS(:year, :month, 1)
        AND c.period_start <  DATEADD(MONTH, 1, DATEFROMPARTS(:year, :month, 1))
      """, nativeQuery = true)
    Optional<BigDecimal> getMonthly(@Param("meterId") UUID meterId,
                                    @Param("year") int year,
                                    @Param("month") int month);

    // B) findMonthlyKwhSameMonthOfYear — mes homólogo, años previos
    @Query(value = """
      SELECT TOP (:limit) c.kwh
      FROM consumptions c
      WHERE c.meter_id = :meterId
        AND c.consumption_type = 'MONTHLY'
        AND MONTH(c.period_start) = :month
        AND c.period_start < :currentStart
      ORDER BY c.period_start DESC
      """, nativeQuery = true)
    List<BigDecimal> findMonthlyKwhSameMonthOfYear(
            @Param("meterId") UUID meterId,
            @Param("month") int month,
            @Param("currentStart") LocalDate currentStart,
            @Param("limit") int limit);

    // C) findPreviousMonthKwh
    @Query(value = """
      SELECT TOP 1 c.kwh
      FROM consumptions c
      WHERE c.meter_id = :meterId
        AND c.consumption_type = 'MONTHLY'
        AND c.period_start < :currentStart
      ORDER BY c.period_start DESC
      """, nativeQuery = true)
    Optional<BigDecimal> findPreviousMonthKwh(@Param("meterId") UUID meterId,
                                              @Param("currentStart") LocalDate currentStart);
}
