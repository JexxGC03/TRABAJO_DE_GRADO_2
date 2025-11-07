package com.ucdc.backend.infrastructure.persistence.entity;

import com.ucdc.backend.domain.enums.Role;
import com.ucdc.backend.domain.enums.UserStatus;
import com.ucdc.backend.domain.value.Email;
import com.ucdc.backend.infrastructure.persistence.converter.EmailConverter;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_service_number", columnNames = "service_number"),
                @UniqueConstraint(name = "uk_user_citizen_id", columnNames = "citizen_id")
        },
        indexes = {
                @Index(name = "ix_user_email", columnList = "email"),
                @Index(name = "ix_user_service_number", columnList = "service_number"),
                @Index(name = "ix_user_fullname", columnList = "full_name")
        }
)
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "full_name", length = 150, nullable = false)
    private String fullName;

    @Convert(converter = EmailConverter.class)
    @Column(nullable = false, unique = true, length = 255)
    private Email email;

    @Column(name = "citizen_id", length = 40, nullable = false)
    private String citizenId;

    @Column(name = "service_number", length = 50, nullable = false)
    private String serviceNumber;

    @Column(name = "phone", length = 32)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 16, nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private UserStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    /* ==================== RELACIONES ==================== */

    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<MeterEntity> meters = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecommendationEntity> recommendations = new ArrayList<>();

}