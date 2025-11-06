package com.ucdc.backend.domain.enums;

public enum UserStatus {

    ACTIVE,
    BLOCKED;

    public boolean isActive()  { return this == ACTIVE; }
    public boolean isBlocked() { return this == BLOCKED; }
}
