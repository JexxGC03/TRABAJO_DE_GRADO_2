package com.ucdc.backend.infrastructure.persistence.entity;

import com.ucdc.backend.domain.enums.MeterStatus;
import com.ucdc.backend.domain.enums.MeterType;
import com.ucdc.backend.domain.enums.Provider;
import jakarta.persistence.*;
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
        name = "meters",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_meter_serial", columnNames = {"serial_number"})
        },
        indexes = {
                @Index(name = "ix_meter_user", columnList = "user_id"),
                @Index(name = "ix_meter_status", columnList = "status")
        }
)
@Entity
public class MeterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_meter_user"))
    private UserEntity user;

    @Column(name = "serial_number", nullable = false, length = 64)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false, length = 16)
    private MeterType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private MeterStatus status;

    @Enumerated(EnumType.STRING) // o String si no usas enum
    @Column(name="provider", length=32, nullable=false)
    private Provider provider;

    @Column(name="installation_address", length=160)
    private String installationAddress;

    @Column(name = "installed_at")
    private LocalDateTime installedAt;

    @Column(name="service_number", length=32)
    private String serviceNumber;

    @Column(name = "alias", length = 80)
    private String alias;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    /* ==================== RELACIONES ==================== */

    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsumptionEntity> consumptions = new ArrayList<>();

    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlertEntity> alerts = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = createdAt;
        if (installedAt == null) installedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
        installedAt = LocalDateTime.now();
    }

}


