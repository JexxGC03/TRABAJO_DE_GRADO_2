package com.ucdc.backend.infrastructure.persistence.entity;

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
@Table(name = "meter_quotas",
        indexes = {
                @Index(name="ix_mq_meter_valid", columnList = "meter_id, valid_from, valid_to")
        })
public class MeterQuotaEntity {

    @Id
    private UUID id;

    @Column(name="meter_id", nullable=false)
    private UUID meterId;

    @Column(nullable=false, length=16)
    private String periodicity; // "MONTHLY"|"DAILY"

    @Column(name="kwh_limit", nullable=false, precision=18, scale=4)
    private BigDecimal kwhLimit;

    @Column(name="valid_from", nullable=false, columnDefinition="datetimeoffset")
    private OffsetDateTime validFrom;

    @Column(name="valid_to", columnDefinition="datetimeoffset")
    private OffsetDateTime validTo;
}
