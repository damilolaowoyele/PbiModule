package org.fhi360.PbiModule.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;



@Data
@NoArgsConstructor
@Entity
@Table(name = "pbi_payment")
public class PbiPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "lga", nullable = false)
    private String lga;

    @Column(name = "facility", nullable = false)
    private String facility;

    @Column(name = "team", nullable = false)
    private Integer team;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Pattern(regexp = "^(active|inactive)$", message = "Status must be either 'active' or 'inactive'")
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_Date", nullable = false)
    private LocalDate toDate;

    @Column(name = "days_selected", nullable = false)
    private Integer daysSelected;

    @Column(name = "days_worked", nullable = false)
    private Integer daysWorked;

    @Column(name = "transport_per_day", nullable = false)
    private BigDecimal transportPerDay;

    @Column(name = "previous_travel_allowance", nullable = false)
    private BigDecimal previousTravelAdvance;

    @Column(name = "total_transport", nullable = false)
    private BigDecimal totalTransport;

    @Column(name = "number_of_persons_vaccinated", nullable = false)
    private Integer numOfPersonsVaccinated;

    @Column(name = "number_of_vaccinations", nullable = false)
    private Integer numOfVaccinations;

    @Column(name = "cost_per_vaccination", nullable = false)
    private BigDecimal costPerVaccination;

    @Column(name = "total_pbi", nullable = false)
    private BigDecimal totalPbi;

    @Column(name = "total_internet_cost", nullable = false)
    private BigDecimal totalInternetCost;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "bank", nullable = false)
    private String bank;
}


