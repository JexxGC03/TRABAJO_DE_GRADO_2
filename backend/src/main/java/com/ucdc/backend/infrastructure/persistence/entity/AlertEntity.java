package com.ucdc.backend.infrastructure.persistence.entity;

import com.ucdc.backend.domain.enums.AlertStatus;
import com.ucdc.backend.domain.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "alerts",
        indexes = {
                @Index(name = "ix_alert_meter", columnList = "meter_id"),
                @Index(name = "ix_alert_status", columnList = "status"),
                @Index(name="ix_alert_active", columnList="meter_id,type,granularity,period,status")
        }
)
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_alert_meter"))
    private MeterEntity meter;

    @Column(name = "threshold_kwh", nullable = false, precision = 12, scale = 2)
    private BigDecimal thresholdKwh;

    @Column(name = "current_kwh", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentKwh;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private AlertStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable=false, length=32)
    private AlertType type;       // AlertType

    @Column(name = "reason", length=300)
    private String reason;

    @Column(name = "granularity", nullable=false, length=16)
    private String granularity; // "MONTHLY"/"DAILY"

    @Column(name = "period", nullable=false, length=7)
    private String period; // "YYYY-MM" (para DAILY, puedes dejar "YYYY-MM" del día)

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}