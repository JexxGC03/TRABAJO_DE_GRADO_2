package com.ucdc.backend.infrastructure.web.controller;

import com.ucdc.backend.application.usecase.meter.RegisterMeterUseCase;
import com.ucdc.backend.infrastructure.security.CurrentUser;
import com.ucdc.backend.infrastructure.web.dto.meter.MeterResponse;
import com.ucdc.backend.infrastructure.web.dto.meter.RegisterMeterRequest;
import com.ucdc.backend.infrastructure.web.mapper.MeterApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meters")
public class MeterController {

    private final RegisterMeterUseCase registerMeterUseCase;
    private final MeterApiMapper mapper;


    @Operation(
            summary = "Registrar un nuevo medidor",
            description = "Crea el medidor y lo asocia al usuario/servicio correspondiente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Creado",
                            content = @Content(schema = @Schema(implementation = MeterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
                    @ApiResponse(responseCode = "409", description = "Conflicto (duplicado)")
            }
    )
    @PostMapping
    public ResponseEntity<MeterResponse> register(@Valid @RequestBody RegisterMeterRequest request) {
        var cmd = mapper.toCommand(request, CurrentUser.id());
        var result = registerMeterUseCase.handle(cmd);
        var response = mapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
