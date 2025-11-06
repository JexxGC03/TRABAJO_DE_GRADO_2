package com.ucdc.backend.application.services.recommendation;

import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsCommand;
import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsResult;
import com.ucdc.backend.application.mapper.RecommendationAppMapper;
import com.ucdc.backend.application.services.recommendation.rules.MonthlyPointEngine;
import com.ucdc.backend.application.services.recommendation.rules.RecommendationRuleEngine;
import com.ucdc.backend.application.usecase.recommendation.GenerateRecommendationsUseCase;
import com.ucdc.backend.domain.enums.RecommendationStatus;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.model.Recommendation;
import com.ucdc.backend.domain.repositories.AlertRepository;
import com.ucdc.backend.domain.repositories.ConsumptionRepository;
import com.ucdc.backend.domain.repositories.RecommendationRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GenerateRecommendationsService implements GenerateRecommendationsUseCase {

    private final UserRepository userRepo;
    private final ConsumptionRepository consumptionRepo; // <-- antes: ConsumptionRepositoryPort
    private final AlertRepository alertRepo;
    private final RecommendationRepository recoRepo;
    private final RecommendationAppMapper mapper;
    private final RecommendationRuleEngine ruleEngine;

    @Override
    @Transactional
    public GenerateRecommendationsResult handle(GenerateRecommendationsCommand cmd) {
        // 1) Validación de usuario
        if (!userRepo.existsById(cmd.userId())) {
            throw new NotFoundException("User", cmd.userId());
        }

        // 2) Datos base
        YearMonth period = cmd.analysisPeriod();
        List<ConsumptionRepository.MonthlyPoint> rawMonthly =
                consumptionRepo.findMonthlyByMeterAndYear(cmd.meterId(), period.getYear());
        if (rawMonthly == null || rawMonthly.isEmpty()) {
            throw new IllegalArgumentException("Insufficient data for analysis");
        }
        var recentAlerts = alertRepo.findRecentByMeter(cmd.meterId());

        // 3) Adaptar a interfaz del motor (sin casts de List)
        record MP(YearMonth period, BigDecimal kwh) implements MonthlyPointEngine {}
        int year = period.getYear();
        List<MonthlyPointEngine> monthly = rawMonthly.stream()
                .map(p -> new MP(YearMonth.of(year, p.month()), p.kwh()))
                .collect(Collectors.toList());

        // 4) Reglas (mockeable en tests)
        List<String> candidateMessages = ruleEngine.build(cmd, monthly, recentAlerts);
        if (candidateMessages == null || candidateMessages.isEmpty()) {
            return new GenerateRecommendationsResult(List.of());
        }
        candidateMessages = candidateMessages.stream()
                .map(String::trim).filter(s -> !s.isBlank()).distinct().toList();

        // 5) Idempotencia por (userId, message)
        Map<String, Recommendation> existingByMessage =
                recoRepo.findByUserAndMessages(cmd.userId(), new HashSet<>(candidateMessages))
                        .stream()
                        .collect(Collectors.toMap(Recommendation::message, r -> r, (a, b) -> a));

        List<Recommendation> toPersist = new ArrayList<>();
        for (String msg : candidateMessages) {
            var existing = existingByMessage.get(msg);
            if (existing == null) {
                toPersist.add(Recommendation.create(UUID.randomUUID(), cmd.userId(), msg));
            } else if (existing.status() == RecommendationStatus.ARCHIVED) {
                existing.activate(); // reactiva y actualiza updatedAt
                toPersist.add(existing);
            }
            // si está ACTIVE → no duplicar
        }

        var saved = toPersist.isEmpty() ? List.<Recommendation>of() : recoRepo.saveAll(toPersist);

        // 6) Resultado: activos previos + guardados, únicos por mensaje
        var resultMap = new LinkedHashMap<String, Recommendation>();
        existingByMessage.values().stream()
                .filter(r -> r.status() == RecommendationStatus.ACTIVE)
                .forEach(r -> resultMap.putIfAbsent(r.message(), r));
        for (var r : saved) resultMap.put(r.message(), r);

        var items = resultMap.values().stream()
                .map(mapper::toItem)
                .toList();

        return new GenerateRecommendationsResult(items);
    }
}
