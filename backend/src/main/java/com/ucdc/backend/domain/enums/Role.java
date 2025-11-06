package com.ucdc.backend.domain.enums;

public enum Role {

    CLIENT,
    ADMIN;

    public boolean isAdmin()  { return this == ADMIN; }
    public boolean isClient() { return this == CLIENT; }
}
