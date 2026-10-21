package com.hims.customer.insurance;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsurancePolicyResponse(

        Long id,
        String policyNumber,
        Long customerId,
        InsuranceType insuranceType,
        boolean roadsideAssistance,
        PaymentFrequency paymentFrequency,
        String vehicleManufacturer,
        String vehicleModel,
        String licensePlate,
        Integer firstRegistrationYear,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal annualPremium,
        BigDecimal installmentAmount,
        PolicyStatus status

) {
}