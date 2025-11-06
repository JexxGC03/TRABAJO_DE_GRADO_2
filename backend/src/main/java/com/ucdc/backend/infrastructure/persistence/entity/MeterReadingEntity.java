package com.ucdc.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "meter_readings",
        uniqueConstraints = @UniqueConstraint(name = "uk_meter_ts", columnNames = {"meter_id", "ts"}),
        indexes = {
                @Index(name = "ix_reading_meter_ts", columnList = "meter_id, ts")
        })
public class MeterReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "meter_id", nullable = false)
    private UUID meterId;

    @Column(name = "ts", nullable = false)
    private OffsetDateTime ts;

    @Column(name = "kwh_accum", nullable = false, precision = 18, scale = 6)
    private BigDecimal kwhAccum;
}
