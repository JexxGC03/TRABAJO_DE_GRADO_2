package com.ucdc.backend.domain.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

public class StatisticalAlertPolicy {

    public record BaselineStats(BigDecimal meanKwh, BigDecimal varianceKwh2,
                                BigDecimal stddevKwh, int n) {}

    private static final MathContext MC = new MathContext(34, RoundingMode.HALF_UP);

    public static BaselineStats statsOf(List<BigDecimal> samples) {
        if (samples == null || samples.size() < 2) throw new IllegalArgumentException("At least 2 samples");
        var n = samples.size();
        var sum = samples.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        var mean = sum.divide(BigDecimal.valueOf(n), MC);
        var sqSum = BigDecimal.ZERO;
        for (var x : samples) {
            var d = x.subtract(mean, MC);
            sqSum = sqSum.add(d.multiply(d, MC), MC);
        }
        var variance = sqSum.divide(BigDecimal.valueOf(n - 1L), MC);
        return new BaselineStats(mean, variance, sqrt(variance), n);
    }

    public static BigDecimal upperBound(BaselineStats s, BigDecimal k) {
        return s.meanKwh().add(s.stddevKwh().multiply(k, MC), MC);
    }

    public static boolean isUpperAnomaly(BigDecimal value, BaselineStats s, BigDecimal k) {
        return value.compareTo(upperBound(s, k)) > 0;
    }

    private static BigDecimal sqrt(BigDecimal x) {
        if (x.signum() <= 0) return x.signum() == 0 ? BigDecimal.ZERO : fail();
        BigDecimal g = new BigDecimal(Math.sqrt(x.doubleValue()), MC);
        for (int i = 0; i < 20; i++) g = g.add(x.divide(g, MC)).divide(BigDecimal.valueOf(2), MC);
        return g;
    }
    private static BigDecimal fail() { throw new IllegalArgumentException("Negative variance"); }
}
