package com.ucdc.backend.domain.model;

import com.ucdc.backend.domain.enums.MeterStatus;
import com.ucdc.backend.domain.enums.MeterType;
import com.ucdc.backend.domain.enums.Provider;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Meter {
    // Identidad
    private final UUID id;
    private UUID userId;

    // Atributos
    private String serialNumber;
    private MeterType type;
    private MeterStatus status;
    private Provider provider;
    private String installationAddress; // 160 máx
    private OffsetDateTime installedAt;
    private String serviceNumber;       // 32 máx
    private String alias;

    // Timestamps de dominio (persistidos por la capa infra)
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // ---------- Constructores/fábricas ----------
    private Meter(UUID id,
                  UUID userId,
                  String serialNumber,
                  MeterType type,
                  MeterStatus status,
                  Provider provider,
                  String installationAddress,
                  String serviceNumber,
                  String alias,
                  OffsetDateTime installedAt,
                  OffsetDateTime createdAt,
                  OffsetDateTime updatedAt) {

        this.id = id;
        this.userId = requireNonNull(userId, "userId");
        this.serialNumber = validateSerial(serialNumber);
        this.type = requireNonNull(type, "type");
        this.status = requireNonNull(status, "status");
        this.provider = requireNonNull(provider, "provider");
        this.installationAddress = validateAddress(installationAddress);
        this.serviceNumber = validateServiceNumber(serviceNumber);
        this.alias = validateAlias(alias);
        this.installedAt = (updatedAt != null) ? installedAt : this.createdAt;
        this.createdAt = (createdAt != null) ? createdAt : OffsetDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : this.createdAt;

        validateInvariants();
    }

    /**
     * Fábrica para crear un Meter nuevo no instalado aún (por defecto INACTIVE).
     */
    public static Meter register(UUID userId,
                                 String serialNumber,
                                 Provider provider,
                                 String installationAddress,
                                 String serviceNumber,
                                 String alias) {
        return new Meter(null, userId, serialNumber, MeterType.SMART, MeterStatus.ACTIVE, provider, installationAddress, serviceNumber,alias, null, null, null);
    }

    /**
     * Fábrica para rehidratar desde persistencia (cuando ya tienes todos los campos).
     */
    public static Meter rehydrate(UUID id,
                                  UUID userId,
                                  String serialNumber,
                                  MeterType type,
                                  MeterStatus status,
                                  Provider provider,
                                  String installationAddress,
                                  String serviceNumber,
                                  String alias,
                                  OffsetDateTime installedAt,
                                  OffsetDateTime createdAt,
                                  OffsetDateTime updatedAt) {
        return new Meter(id, userId, serialNumber, type, status, provider, installationAddress, serviceNumber, alias,installedAt, createdAt, updatedAt);
    }

    // ---------- Reglas / comportamiento de dominio ----------
    /** Activa el medidor (no permitido desde MAINTENANCE). */
    public void activate() {
        if (status == MeterStatus.MAINTENANCE) {
            throw new IllegalStateException("No se puede activar un medidor en mantenimiento.");
        }
        if (status == MeterStatus.ACTIVE) {
            throw new IllegalStateException("El medidor ya está activo.");
        }
        this.status = MeterStatus.ACTIVE;
        touch();
    }

    /** Desactiva el medidor. */
    public void deactivate() {
        if (status == MeterStatus.INACTIVE) {
            throw new IllegalStateException("El medidor ya está inactivo.");
        }
        this.status = MeterStatus.INACTIVE;
        touch();
    }

    /** Pone el medidor en mantenimiento (solo admins podrían invocarlo desde él use case). */
    public void markMaintenance() {
        if (status == MeterStatus.MAINTENANCE) {
            throw new IllegalStateException("El medidor ya está en mantenimiento.");
        }
        this.status = MeterStatus.MAINTENANCE;
        touch();
    }

    /** Cambia el número serial (no permite vacío y valida longitud). */
    public void changeSerialNumber(String newSerial) {
        this.serialNumber = validateSerial(newSerial);
        touch();
    }

    /**
     * Cambia el tipo de medidor.
     * Regla sugerida: solo permitir cambio si está INACTIVE (para evitar inconsistencias).
     */
    public void changeType(MeterType newType) {
        requireNonNull(newType, "newType");
        if (this.type == newType) return;
        if (this.status != MeterStatus.INACTIVE) {
            throw new IllegalStateException("Solo se puede cambiar el tipo cuando el medidor está INACTIVE.");
        }
        this.type = newType;
        touch();
    }

    /** Reasigna el medidor a otro usuario (p. ej., cambio de titular). */
    public void reassignTo(UUID newUserId) {
        requireNonNull(newUserId, "newUserId");
        if (this.userId.equals(newUserId)) return;
        // Regla sugerida: no permitir reasignación si está ACTIVE
        if (this.status == MeterStatus.ACTIVE) {
            throw new IllegalStateException("No se puede reasignar un medidor ACTIVO. Desactívelo primero.");
        }
        this.userId = newUserId;
        touch();
    }

    /** Marca la fecha de instalación (solo si no estaba instalada). */
    public void markInstalledAt(OffsetDateTime when) {
        if (this.installedAt != null) {
            throw new IllegalStateException("El medidor ya tiene fecha de instalación.");
        }
        this.installedAt = requireNonNull(when, "installedAt");
        touch();
    }

    private static String validateAddress(String s){
        var t=trimToNull(s);
        if(t!=null&&t.length()>160)
            throw new IllegalArgumentException("address too long");
        return t;
    }


    private static String validateServiceNumber(String s){
        var t=trimToNull(s);
        if(t!=null&&t.length()>32)
            throw new IllegalArgumentException("serviceNumber too long");
        return t;
    }

    /** Cambia/define alias del medidor (opcional, máx 80). */
    public void rename(String newAlias) {
        this.alias = validateAlias(newAlias);
        touch();
    }

    // ---------- Getters (sin setters libres) ----------
    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public String serialNumber() { return serialNumber; }
    public MeterType type() { return type; }
    public MeterStatus status() { return status; }
    public Provider provider() { return provider; }
    public String installationAddress() { return installationAddress; }
    public String serviceNumber() { return serviceNumber; }
    public String alias() { return alias; }
    public OffsetDateTime installedAt() { return installedAt; }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updatedAt; }

    // ---------- Utilidades/invariantes ----------
    private void validateInvariants() {
        // Ejemplo: si está ACTIVE debe tener serial no vacío (ya lo garantizamos), etc.
        // Puedes añadir más reglas de negocio globales aquí si aplica.
    }

    private void touch() { this.updatedAt = OffsetDateTime.now(); }

    private static <T> T requireNonNull(T v, String name) {
        return Objects.requireNonNull(v, name + " is required");
    }

    private static String validateSerial(String serial) {
        if (serial == null || serial.isBlank()) {
            throw new IllegalArgumentException("serialNumber is required");
        }
        String s = serial.trim();
        if (s.length() > 64) {
            throw new IllegalArgumentException("serialNumber too long (max 64)");
        }
        // aquí podrías validar formato específico si lo necesitas (regex)
        return s;
    }

    private static String validateAlias(String alias) {
        String t = trimToNull(alias);
        if (t != null && t.length() > 80) {
            throw new IllegalArgumentException("alias too long (max 80)");
        }
        return t;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // Identidad por ID
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meter other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() {
        return "Meter{id=%s, userId=%s, serial=%s, type=%s, status=%s, provider=%s, alias=%s}"
                .formatted(id, userId, serialNumber, type, status, provider, alias);
    }
}
