package com.hims.customer.insurance;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record InsurancePolicyRequest(

        @NotNull
        Long customerId,

        @NotNull
        InsuranceType insuranceType,

        boolean roadsideAssistance,

        @NotNull
        PaymentFrequency paymentFrequency,

        @NotBlank
        String vehicleManufacturer,

        @NotBlank
        String vehicleModel,

        @NotBlank
        String licensePlate,

        @NotNull
        @Min(1900)
        Integer firstRegistrationYear,

        @NotNull
        @FutureOrPresent
        LocalDate startDate

) {
}