package com.ucdc.backend.application.services.recommendation.rules;

import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsCommand;
import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.model.Alert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultRecommendationRuleEngine implements RecommendationRuleEngine {

    // Umbrales (ajustables)
    private static final BigDecimal ALERT_PRESENT_WEIGHT = bd("0.00");   // solo gatilla mensaje, no compara kWh
    private static final BigDecimal MOM_INCREASE = bd("0.20");           // +20% vs mes anterior
    private static final BigDecimal ROLLING6_INCREASE = bd("0.15");      // +15% vs promedio 6 meses
    private static final BigDecimal YOY_INCREASE = bd("0.10");           // +10% vs mismo mes del año pasado

    @Override
    public List<String> build(GenerateRecommendationsCommand cmd,
                              List<? extends MonthlyPointEngine> monthly,
                              List<Alert> recentAlerts) {

        if (monthly == null || monthly.isEmpty()) return List.of();

        // Normalizamos a mapa por período
        Map<YearMonth, BigDecimal> kwhByMonth = monthly.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        MonthlyPointEngine::period,
                        MonthlyPointEngine::kwh,
                        BigDecimal::add // si vinieran duplicados, sumamos
                ));

        YearMonth target = cmd.analysisPeriod();
        BigDecimal current = kwhByMonth.get(target);
        if (current == null) {
            // si no hay dato del mes objetivo, intenta con el último mes disponible
            Optional<YearMonth> last = kwhByMonth.keySet().stream().max(Comparator.naturalOrder());
            if (last.isEmpty()) return List.of();
            target = last.get();
            current = kwhByMonth.get(target);
        }

        List<String> messages = new ArrayList<>();

        /* R1: Alertas recientes activas → recomendación de alto impacto */
        boolean hasActiveAlert = recentAlerts != null
                && recentAlerts.stream().anyMatch(a -> a.status() == AlertStatus.ACTIVE);
        if (hasActiveAlert) {
            messages.add("Se detectó sobreconsumo reciente. Revisa cargas en horario pico y desplázalas a horas valle para reducir el costo.");
        }

        /* R2: Mes a mes (MoM) +20% vs mes anterior */
        YearMonth prev = target.minusMonths(1);
        if (kwhByMonth.containsKey(prev)) {
            var prevKwh = kwhByMonth.get(prev);
            if (isIncrease(current, prevKwh, MOM_INCREASE)) {
                messages.add("Tu consumo creció más del 20% respecto al mes anterior. Verifica standby de electrodomésticos y hábitos de uso.");
            }
        }

        /* R3: +15% vs promedio móvil de 6 meses */
        var rolling6 = rollingAverage(kwhByMonth, target, 6);
        if (rolling6 != null && isIncrease(current, rolling6, ROLLING6_INCREASE)) {
            messages.add("El consumo del mes supera en más del 15% tu promedio de los últimos 6 meses. Considera programar lavadora y planchar en horas valle.");
        }

        /* R4: +10% vs mismo mes del año pasado (YoY) */
        YearMonth lastYear = target.minusYears(1);
        if (kwhByMonth.containsKey(lastYear) && isIncrease(current, kwhByMonth.get(lastYear), YOY_INCREASE)) {
            messages.add("Subiste más del 10% respecto al mismo mes del año pasado. Revisa fugas, resistencias o equipos con bajo rendimiento.");
        }

        /* R5: Consumo base alto (heurística simple) → si el promedio 6M es alto y no baja, sugiere eficiencia */
        if (rolling6 != null && current.compareTo(rolling6) > 0 && rolling6.compareTo(bd("0")) > 0) {
            messages.add("Optimiza el consumo base: reemplaza bombillas por LED y desconecta cargadores cuando no los uses.");
        }

        // Limpiar: trim, dedup, longitud <= 500
        return messages.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(DefaultRecommendationRuleEngine::cap500)
                .distinct()
                .toList();
    }

    /* ===== helpers ===== */

    private static boolean isIncrease(BigDecimal now, BigDecimal base, BigDecimal threshold) {
        if (now == null || base == null || base.signum() <= 0) return false;
        // (now - base) / base >= threshold
        BigDecimal delta = now.subtract(base);
        if (delta.signum() <= 0) return false;
        BigDecimal ratio = delta.divide(base, 6, RoundingMode.HALF_UP);
        return ratio.compareTo(threshold) >= 0;
    }

    private static BigDecimal rollingAverage(Map<YearMonth, BigDecimal> byMonth, YearMonth endInclusive, int months) {
        var list = new ArrayList<BigDecimal>();
        for (int i = 0; i < months; i++) {
            var ym = endInclusive.minusMonths(i);
            var v = byMonth.get(ym);
            if (v != null) list.add(v);
        }
        if (list.isEmpty()) return null;
        BigDecimal sum = list.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(list.size()), 6, RoundingMode.HALF_UP);
    }

    private static BigDecimal bd(String v) { return new BigDecimal(v); }

    private static String cap500(String s) {
        return s.length() <= 500 ? s : s.substring(0, 500);
    }
}
