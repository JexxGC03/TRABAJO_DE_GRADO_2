package com.ucdc.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_credentials",
        uniqueConstraints = @UniqueConstraint(name = "uk_cred_user", columnNames = "user_id"))
public class PasswordCredentialEntity {

    /** FK = PK (1:1 con User). Lo estableces manualmente con el id del usuario. */
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** Relación solo-lectura para navegación; NO escribe la FK (la escribe userId). */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_cred_user"),
            insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /** Control de concurrencia optimista (opcional pero recomendado). */
    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onInsert() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** Igualdad por PK compartida (userId). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordCredentialEntity that)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
