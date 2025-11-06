package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.dto.meter.ListMyMetersQuery;
import com.ucdc.backend.application.usecase.meter.ListMyMetersUseCase;
import com.ucdc.backend.application.usecase.meter.UpdateMeterUseCase;
import com.ucdc.backend.infrastructure.security.CurrentUser; // tu helper
import com.ucdc.backend.infrastructure.web.dto.meter.MeterItemResponse;
import com.ucdc.backend.infrastructure.web.dto.meter.MeterUpdateRequest;
import com.ucdc.backend.infrastructure.web.mapper.MeterApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/my/meters")
@RequiredArgsConstructor
@Validated
public class MyMetersController {

    private final ListMyMetersUseCase listUseCase;
    private final UpdateMeterUseCase updateUseCase;
    private final MeterApiMapper mapper;

    @GetMapping
    public List<MeterItemResponse> listMine() {
        var userId = CurrentUser.id(); // de tu contexto de seguridad
        var result = listUseCase.handle(new ListMyMetersQuery(userId));   // <-- FIX
        return result.items().stream().map(mapper::toMeterItem).toList();
    }

    @PutMapping("/{meterId}")
    @ResponseStatus(HttpStatus.OK)
    public MeterItemResponse update(
            @PathVariable UUID meterId,
            @Valid @RequestBody MeterUpdateRequest request
    ) {
        var userId = CurrentUser.id();
        var dto = updateUseCase.handle(mapper.toUpdate(userId, meterId, request));
        return mapper.toMeterItem(dto);
    }
}
