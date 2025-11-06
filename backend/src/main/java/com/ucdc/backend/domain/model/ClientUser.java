package com.ucdc.backend.domain.model;

import com.ucdc.backend.domain.enums.Role;
import com.ucdc.backend.domain.enums.UserStatus;
import com.ucdc.backend.domain.value.*;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class ClientUser extends User {

    public ClientUser(UUID id, String fullName, Email email, CitizenId citizenId,
                      ServiceNumber serviceNumber, Phone phone,
                      UserStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        super(id, fullName, email, citizenId, serviceNumber, phone,
                Role.CLIENT, status, createdAt, updatedAt);
    }


    public boolean canSeeOnlyOwnData() { return true; }

    public static ClientUser create(UUID id,
                                    String fullName,
                                    Email email,
                                    CitizenId citizenId,
                                    ServiceNumber serviceNumber,
                                    Phone phone) {
        return new ClientUser(
                id,
                fullName,
                email,
                citizenId,
                serviceNumber,
                phone,
                UserStatus.ACTIVE,           // por defecto activo al crear
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }
}
