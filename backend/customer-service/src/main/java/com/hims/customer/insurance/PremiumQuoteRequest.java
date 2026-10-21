package com.hims.customer.insurance;

import jakarta.validation.constraints.NotNull;

public record PremiumQuoteRequest(

        @NotNull
        InsuranceType insuranceType,

        boolean roadsideAssistance,

        @NotNull
        PaymentFrequency paymentFrequency

) {
}