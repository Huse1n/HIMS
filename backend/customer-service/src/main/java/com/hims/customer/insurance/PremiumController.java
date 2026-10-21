package com.hims.customer.insurance;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
public class PremiumController {

    private final PremiumCalculator premiumCalculator;

    public PremiumController(PremiumCalculator premiumCalculator) {
        this.premiumCalculator = premiumCalculator;
    }

    @PostMapping("/quote")
    public PremiumQuote calculate(
            @Valid @RequestBody PremiumQuoteRequest request
    ) {
        return premiumCalculator.calculate(
                request.insuranceType(),
                request.roadsideAssistance(),
                request.paymentFrequency()
        );
    }
}