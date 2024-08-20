package org.fhi360.PbiModule.Model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentDetails(
        String firstName,
        String lastName,
        String state,
        String lga,
        String facility,
        String team,
        String designation,
        String status,
        LocalDate fromDate,
        LocalDate toDate,
        Integer daysSelected,
        Integer daysWorked,
        BigDecimal transportPerDay,
        BigDecimal previousTravelAllowance,
        BigDecimal totalTransport,
        Integer numberOfVaccinations,
        BigDecimal costPerVaccination,
        BigDecimal totalPbi,
        BigDecimal totalInternetCost,
        BigDecimal total,
        String accountNumber,
        String accountName,
        String bank
) {}
