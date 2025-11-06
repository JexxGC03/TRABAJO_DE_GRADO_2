package com.ucdc.backend.domain.model;

import com.ucdc.backend.domain.enums.Role;
import com.ucdc.backend.domain.enums.UserStatus;
import com.ucdc.backend.domain.value.*;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class AdminUser extends User{

    public AdminUser(UUID id, String fullName, Email email, CitizenId citizenId,
                     ServiceNumber serviceNumber, Phone phone,
                     UserStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        super(id, fullName, email, citizenId, serviceNumber, phone,
                Role.ADMIN, status, createdAt, updatedAt);
    }

    public boolean canManageUsers() { return true; }
}
