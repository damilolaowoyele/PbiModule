package org.fhi360.PbiModule.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "payment_setting")
public class PaymentSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @NonNull
    @Column(name = "cost_per_immunization", nullable = false)
    private BigDecimal costPerVaccination;

    @NonNull
    @Column(name = "daily_transport_cost", nullable = false)
    private BigDecimal dailyTransportCost;

    @NonNull
    @Column(name = "weekly_internet_cost", nullable = false)
    private BigDecimal weeklyInternetCost;

    @NonNull
    @Column(name = "team_type", nullable = false)
    private String teamType;

}
