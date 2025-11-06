package com.ucdc.backend.infrastructure.persistence.entity;

import com.ucdc.backend.domain.enums.ConsumptionType;
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
@Table(name = "consumptions",
        uniqueConstraints = @UniqueConstraint(name = "uk_cons_key", columnNames = {"meter_id", "consumption_type", "period_start"}),
        indexes = {
                @Index(name = "ix_cons_meter_type_start", columnList = "meter_id, consumption_type, period_start"),
                @Index(name = "ix_cons_meter_type_day", columnList = "meter_id, consumption_type")
        })
@Entity
public class ConsumptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_consumption_meter"))
    private MeterEntity meter;

    @Enumerated(EnumType.STRING)
    @Column(name = "consumption_type", nullable = false, length = 16)
    private ConsumptionType consumptionType; // MINUTELY, HOURLY, DAILY, MONTHLY

    @Column(name = "period_start", nullable = false)
    private OffsetDateTime periodStart;

    @Column(name = "kwh", nullable = false, precision = 18, scale = 6)
    private BigDecimal kwh;

}