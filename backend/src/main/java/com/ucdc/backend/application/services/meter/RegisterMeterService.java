package com.ucdc.backend.application.services.meter;

import com.ucdc.backend.application.dto.meter.RegisterMeterCommand;
import com.ucdc.backend.application.dto.meter.RegisterMeterResult;
import com.ucdc.backend.application.mapper.MeterAppMapper;
import com.ucdc.backend.application.usecase.meter.RegisterMeterUseCase;
import com.ucdc.backend.domain.exceptions.logic.BadRequestException;
import com.ucdc.backend.domain.exceptions.logic.ConflictException;
import com.ucdc.backend.domain.exceptions.logic.NotFoundException;
import com.ucdc.backend.domain.model.Meter;
import com.ucdc.backend.domain.repositories.MeterRepository;
import com.ucdc.backend.domain.repositories.UserRepository;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterMeterService implements RegisterMeterUseCase {

    private static final Pattern SERIAL_RX = Pattern.compile("^[A-Z0-9\\-]{4,32}$");
    private static final Pattern SERVICE_RX = Pattern.compile("^[0-9]{4,20}$");

    private final UserRepository userRepo;
    private final MeterRepository meterRepo;
    private final MeterAppMapper mapper;

    @Override
    public RegisterMeterResult handle(RegisterMeterCommand cmd) {
        // 0) sanity
        if (cmd.userId() == null) throw new BadRequestException("Missing userId");        // 400
        if (isBlank(cmd.serialNumber())) throw new BadRequestException("serialNumber is required");
        if (isBlank(cmd.serviceNumber())) throw new BadRequestException("serviceNumber is required");
        if (isBlank(cmd.installationAddress())) throw new BadRequestException("installationAddress is required");
        if (cmd.provider() == null) throw new BadRequestException("provider is required"); // enum

        // 1) usuario existe
        if (userRepo.findById(cmd.userId()).isEmpty()) {
            throw new NotFoundException("User not found: " + cmd.userId());               // 404
        }

        // 2) normalizar inputs
        var serial = cmd.serialNumber().trim().toUpperCase();
        var service = cmd.serviceNumber().trim();
        var address = cmd.installationAddress().trim();
        var alias = cmd.alias() != null ? cmd.alias().trim() : null;

        // 3) formato básico
        if (!SERIAL_RX.matcher(serial).matches()) {
            throw new BadRequestException("Invalid serialNumber format");                 // 400
        }
        if (!SERVICE_RX.matcher(service).matches()) {
            throw new BadRequestException("Invalid serviceNumber format");                // 400
        }
        if (address.length() < 3 || address.length() > 200) {
            throw new BadRequestException("installationAddress length must be 3..200");  // 400
        }
        if (alias != null && alias.length() > 60) {
            throw new BadRequestException("alias max length is 60");                      // 400
        }

        // 4) unicidades
        if (meterRepo.existsBySerialNumber(serial)) {
            throw new ConflictException("Serial already exists: ", serial);              // 409
        }
        // si tu negocio lo requiere: serviceNumber único por proveedor
        if (meterRepo.existsByProviderAndServiceNumber(cmd.provider(), service)) {
            throw new ConflictException("serviceNumber already registered for provider", cmd.serviceNumber()); // 409
        }
        // alias único por usuario
        if (alias != null && meterRepo.existsByUserAndAlias(cmd.userId(), alias)) {
            throw new ConflictException("alias already in use for this user", alias);            // 409
        }

        // 5) construir dominio con valores normalizados
        var normalized = new RegisterMeterCommand(
                cmd.userId(), serial, cmd.provider(), service, address, alias
        );
        log.info("📡 Normalized received:");
        log.info(" serviceNumber={} address={}",
                 normalized.serviceNumber(), normalized.installationAddress());


        Meter meter = mapper.toDomain(normalized);
        log.info("📡 meter received:");
        log.info(" serviceNumber={} address={}",
                meter.serviceNumber(), meter.installationAddress());
        Meter saved = meterRepo.save(meter);
        log.info("📡 Saved received:");
        log.info(" serviceNumber={} address={}",
                saved.serviceNumber(), saved.installationAddress());
        return mapper.toResult(saved);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
