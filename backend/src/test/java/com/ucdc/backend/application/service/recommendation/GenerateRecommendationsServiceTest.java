package com.ucdc.backend.application.service.recommendation;

import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsCommand;
import com.ucdc.backend.application.dto.recommendation.GenerateRecommendationsResult;
import com.ucdc.backend.application.mapper.RecommendationAppMapper;
import com.ucdc.backend.application.services.recommendation.GenerateRecommendationsService;
import com.ucdc.backend.application.services.recommendation.rules.RecommendationRuleEngine;
import com.ucdc.backend.domain.enums.RecommendationStatus;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.model.Recommendation;
import com.ucdc.backend.domain.repositories.AlertRepository;
import com.ucdc.backend.domain.repositories.RecommendationRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import com.ucdc.backend.domain.value.ConsumptionMonthlyPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateRecommendationsServiceTest {

    @Mock
    UserRepository userRepo;
    @Mock
    ConsumptionRepositoryPort consumptionRepo;
    @Mock
    AlertRepository alertRepo;
    @Mock
    RecommendationRepository recoRepo;
    @Mock
    RecommendationAppMapper mapper;
    @Mock
    RecommendationRuleEngine ruleEngine;

    GenerateRecommendationsService service;

    UUID userId = UUID.randomUUID();
    UUID meterId = UUID.randomUUID();
    YearMonth month = YearMonth.now();

    @BeforeEach
    void setUp() {
        service = new GenerateRecommendationsService(
                userRepo, consumptionRepo, alertRepo, recoRepo, mapper, ruleEngine
        );
    }

    @Test
    void whenUserNotFound_thenThrow404() {
        var cmd = new GenerateRecommendationsCommand(userId, meterId, month);
        when(userRepo.existsById(userId)).thenReturn(false);

        var ex = assertThrows(NotFoundException.class, () -> service.handle(cmd));
        assertTrue(ex.getMessage().contains("User not found"));
        verifyNoInteractions(consumptionRepo, ruleEngine, recoRepo);
    }

    @Test
    void whenNoMonthlyData_thenThrow400() {
        var cmd = new GenerateRecommendationsCommand(userId, meterId, month);

        when(userRepo.existsById(userId)).thenReturn(true);;
        when(consumptionRepo.findMonthlyByMeterAndYear(meterId, month.getYear()))
                .thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> service.handle(cmd));
    }

    @Test
    void whenCandidates_thenCreateReactivateAndReturnUniqueActives() {
        var cmd = new GenerateRecommendationsCommand(userId, meterId, month);

        when(userRepo.existsById(userId)).thenReturn(true);

        // Datos mensuales (usa tu clase real ConsumptionMonthlyPoint)
        var month = YearMonth.now();
        var pPrev = mock(ConsumptionMonthlyPoint.class);
        when(pPrev.month()).thenReturn(month.minusMonths(1).getMonthValue()); // 🔸 devuelve un int 1..12
        when(pPrev.kwh()).thenReturn(new BigDecimal("100"));

        var pNow = mock(ConsumptionMonthlyPoint.class);
        when(pNow.month()).thenReturn(month.getMonthValue());                 // 🔸 mes actual (int)
        when(pNow.kwh()).thenReturn(new BigDecimal("130"));

        when(consumptionRepo.findMonthlyByMeterAndYear(meterId, month.getYear()))
                .thenReturn(List.of(pPrev, pNow));

        when(alertRepo.findRecentByMeter(meterId)).thenReturn(Collections.emptyList());

        // Mensajes generados por el motor
        var msgs = List.of("Reduce consumo en horas pico", "Revisa standby");
        when(ruleEngine.build(any(), anyList(), anyList())).thenReturn(msgs);

        // En repos ya existe uno ACTIVE y otro ARCHIVED
        var existingActive = Recommendation.rehydrate(UUID.randomUUID(), userId, msgs.get(0),
                RecommendationStatus.ACTIVE, null, null);
        var existingArchived = Recommendation.rehydrate(UUID.randomUUID(), userId, msgs.get(1),
                RecommendationStatus.ARCHIVED, null, null);

        when(recoRepo.findByUserAndMessages(eq(userId), eq(new HashSet<>(msgs))))
                .thenReturn(List.of(existingActive, existingArchived));

        // saveAll devuelve lo que recibe
        when(recoRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        // mapper a DTO
        when(mapper.toItem(any())).thenAnswer(inv -> {
            var r = (Recommendation) inv.getArgument(0);
            return new GenerateRecommendationsResult.RecommendationItem(r.message(), r.status().name());
        });

        var result = service.handle(cmd);

        assertEquals(2, result.recommendations().size());
        var messages = result.recommendations().stream()
                .map(GenerateRecommendationsResult.RecommendationItem::message)
                .toList();
        assertTrue(messages.containsAll(msgs));

        // Verifica que se reactivó el ARCHIVED y se guardó
        verify(recoRepo).saveAll(argThat(list ->
                list.size() == 1 && list.get(0).message().equals(msgs.get(1))
                        && list.get(0).status() == RecommendationStatus.ACTIVE
        ));
    }
}
