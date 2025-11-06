package com.ucdc.backend.domain.model;

import com.ucdc.backend.domain.enums.Role;
import com.ucdc.backend.domain.enums.UserStatus;
import com.ucdc.backend.domain.value.CitizenId;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.domain.value.Phone;
import com.ucdc.backend.domain.value.ServiceNumber;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public sealed abstract class User permits AdminUser, ClientUser {

    // Identidad
    private final UUID id;

    // Datos centrales
    private String fullName;
    private Email email;
    private CitizenId citizenId;
    private ServiceNumber serviceNumber;
    private Phone phone;

    // Estado y rol
    private UserStatus status;
    private final Role role;

    // Timestamps (dominio puede llevarlos; la infra los persistirá)
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // -------------------- ctor protegido --------------------
    protected User(UUID id,
            String fullName,
            Email email,
            CitizenId citizenId,
            ServiceNumber serviceNumber,
            Phone phone,
            Role role,
            UserStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;
        this.fullName = requireNonBlank(fullName, "fullName is required");
        this.email = Objects.requireNonNull(email, "email is required");
        this.citizenId = Objects.requireNonNull(citizenId, "citizenId is required");
        this.serviceNumber = Objects.requireNonNull(serviceNumber, "serviceNumber is required");
        this.phone = Objects.requireNonNull(phone, "phone is required");
        this.role = Objects.requireNonNull(role, "role is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.createdAt = Objects.requireNonNullElseGet(createdAt, OffsetDateTime::now);
        this.updatedAt = Objects.requireNonNullElseGet(updatedAt, OffsetDateTime::now);
        validateInvariants();
    }

    // -------------------- fábricas estáticas --------------------
    public static AdminUser newAdmin(UUID id,
            String fullName,
            Email email,
            CitizenId citizenId,
            ServiceNumber serviceNumber,
            Phone phone) {
        return new AdminUser(id, fullName, email, citizenId, serviceNumber, phone,
                UserStatus.ACTIVE, null, null);
    }

    public static ClientUser newClient(UUID id,
            String fullName,
            Email email,
            CitizenId citizenId,
            ServiceNumber serviceNumber,
            Phone phone) {
        return new ClientUser(id, fullName, email, citizenId, serviceNumber, phone,
                UserStatus.ACTIVE, null, null);
    }

    /** Mutador fluido usado por tu servicio: user.withStatus(UserStatus.BLOCKED) */
    public User withStatus(UserStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "newStatus");
        this.updatedAt = OffsetDateTime.now();
        return this; // permite encadenar si quisieras
    }

    // Update
    public void update(String newFullName, Email newEmail, Phone newPhone) {
        // fullName (si viene, valida no blank)
        if (newFullName != null) {
            this.fullName = requireNonBlank(newFullName, "fullName is required");
        }
        // email (si viene, ya viene validado por el VO)
        if (newEmail != null) {
            this.email = newEmail;
        }
        // phone (si viene, ya viene validado por el VO)
        if (newPhone != null) {
            this.phone = newPhone;
        }

        // actualizar timestamp y revalidar invariantes
        this.updatedAt = OffsetDateTime.now();
        validateInvariants();
    }

    // -------------------- invariantes --------------------
    private void validateInvariants() {
        if (role == Role.ADMIN && status == UserStatus.BLOCKED) {
            // ejemplo de invariante: un admin bloqueado podría estar prohibido en tu negocio
            // (si no aplica, elimina esta regla)
        }
    }

    // -------------------- comportamiento común --------------------
    public void rename(String newFullName) {
        this.fullName = requireNonBlank(newFullName, "fullName is required");
        touch();
    }

    public void changeEmail(Email newEmail) {
        this.email = Objects.requireNonNull(newEmail, "email is required");
        touch();
    }

    public void changePhone(Phone newPhone) {
        this.phone = Objects.requireNonNull(newPhone, "phone is required");
        touch();
    }

    public void block() {
        if (this.status == UserStatus.BLOCKED) {
            throw new IllegalStateException("user already blocked");
        }
        this.status = UserStatus.BLOCKED;
        touch();
    }

    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            throw new IllegalStateException("user already active");
        }
        this.status = UserStatus.ACTIVE;
        touch();
    }

    protected void touch() { this.updatedAt = OffsetDateTime.now(); }

    // -------------------- getters (sin setters libres) --------------------
    public UUID id() { return id; }
    public String fullName() { return fullName; }
    public Email email() { return email; }
    public CitizenId citizenId() { return citizenId; }
    public ServiceNumber serviceNumber() { return serviceNumber; }
    public Phone phone() { return phone; }
    public UserStatus status() { return status; }
    public Role role() { return role; }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updatedAt; }

    public boolean isAdmin()  { return role.isAdmin(); }
    public boolean isClient() { return role.isClient(); }

    // Identidad por id
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }

    private static String requireNonBlank(String v, String msg) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(msg);
        return v;
    }
}
