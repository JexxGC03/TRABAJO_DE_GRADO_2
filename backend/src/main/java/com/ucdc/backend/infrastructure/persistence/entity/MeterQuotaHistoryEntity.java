package com.ucdc.backend.infrastructure.persistence.entity;

import com.ucdc.backend.domain.model.MeterQuota;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "meter_quota_history",
        indexes = {
                @Index(name = "ix_mqh_meter_valid_from", columnList = "meter_id, valid_from")
        })
public class MeterQuotaHistoryEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private MeterEntity meter;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodicity", nullable = false, length = 16)
    private MeterQuota.Periodicity periodicity;

    @Column(name = "kwh_limit", nullable = false, precision = 12, scale = 2)
    private BigDecimal kwhLimit;

    @Column(name = "valid_from", nullable = false, columnDefinition = "datetimeoffset")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to", nullable = false, columnDefinition = "datetimeoffset")
    private OffsetDateTime validTo;

    @Column(name = "created_at", nullable = false, columnDefinition = "datetimeoffset")
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
