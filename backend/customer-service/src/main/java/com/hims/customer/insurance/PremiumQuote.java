package com.hims.customer.insurance;

import java.math.BigDecimal;

public record PremiumQuote(
        BigDecimal basePremium,
        BigDecimal annualPremium,
        BigDecimal installmentAmount,
        int installmentsPerYear
) {
}