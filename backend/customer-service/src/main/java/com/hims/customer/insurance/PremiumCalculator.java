package com.hims.customer.insurance;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PremiumCalculator {

    public PremiumQuote calculate(
            InsuranceType insuranceType,
            boolean roadsideAssistance,
            PaymentFrequency paymentFrequency
    ) {

        // 1. Grundpreis
        BigDecimal basePremium = switch (insuranceType) {
            case LIABILITY -> new BigDecimal("300.00");
            case PARTIAL_COVERAGE -> new BigDecimal("450.00");
            case FULL_COVERAGE -> new BigDecimal("650.00");
        };

        // 2. Schutzbrief
        BigDecimal annualPremium = basePremium;

        if (roadsideAssistance) {
            annualPremium = annualPremium.add(
                    new BigDecimal("30.00")
            );
        }

        // 3. Zahlungsweise
        BigDecimal surcharge = switch (paymentFrequency) {
            case YEARLY -> BigDecimal.ZERO;
            case HALF_YEARLY -> new BigDecimal("0.03");
            case QUARTERLY -> new BigDecimal("0.05");
        };

        int installments = switch (paymentFrequency) {
            case YEARLY -> 1;
            case HALF_YEARLY -> 2;
            case QUARTERLY -> 4;
        };

        // 4. Gesamtbeitrag inklusive Zuschlag
        annualPremium = annualPremium
                .multiply(BigDecimal.ONE.add(surcharge))
                .setScale(2, RoundingMode.HALF_UP);

        // 5. Beitrag pro Zahlung
        BigDecimal installmentAmount = annualPremium.divide(
                BigDecimal.valueOf(installments),
                2,
                RoundingMode.HALF_UP
        );

        return new PremiumQuote(
                basePremium,
                annualPremium,
                installmentAmount,
                installments
        );
    }
}