package com.hims.customer.insurance;

import com.hims.customer.entity.Customer;
import com.hims.customer.repository.CustomerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class InsurancePolicyService {

    private final InsurancePolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final PremiumCalculator premiumCalculator;

    public InsurancePolicyService(
            InsurancePolicyRepository policyRepository,
            CustomerRepository customerRepository,
            PremiumCalculator premiumCalculator
    ) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.premiumCalculator = premiumCalculator;
    }

    // CREATE
    @Transactional
    public InsurancePolicyResponse create(InsurancePolicyRequest request) {

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        PremiumQuote quote = premiumCalculator.calculate(
                request.insuranceType(),
                request.roadsideAssistance(),
                request.paymentFrequency()
        );

        InsurancePolicy policy = new InsurancePolicy();

        policy.setPolicyNumber(
                "KFZ-" + UUID.randomUUID().toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        policy.setCustomer(customer);
        policy.setInsuranceType(request.insuranceType());
        policy.setRoadsideAssistance(request.roadsideAssistance());
        policy.setPaymentFrequency(request.paymentFrequency());

        policy.setVehicleManufacturer(request.vehicleManufacturer());
        policy.setVehicleModel(request.vehicleModel());
        policy.setLicensePlate(request.licensePlate());
        policy.setFirstRegistrationYear(request.firstRegistrationYear());

        policy.setStartDate(request.startDate());
        policy.setEndDate(request.startDate().plusYears(1).minusDays(1));

        policy.setAnnualPremium(quote.annualPremium());
        policy.setInstallmentAmount(quote.installmentAmount());

        policy.setStatus(PolicyStatus.ACTIVE);

        return toResponse(policyRepository.save(policy));
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<InsurancePolicyResponse> findAll() {

        return policyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ ONE
    @Transactional(readOnly = true)
    public InsurancePolicyResponse findById(Long id) {
        return toResponse(findPolicy(id));
    }

    // READ BY CUSTOMER
    @Transactional(readOnly = true)
    public List<InsurancePolicyResponse> findByCustomer(Long customerId) {

        return policyRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // UPDATE
    @Transactional
    public InsurancePolicyResponse update(
            Long id,
            InsurancePolicyRequest request
    ) {

        InsurancePolicy policy = findPolicy(id);

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        PremiumQuote quote = premiumCalculator.calculate(
                request.insuranceType(),
                request.roadsideAssistance(),
                request.paymentFrequency()
        );

        policy.setCustomer(customer);
        policy.setInsuranceType(request.insuranceType());
        policy.setRoadsideAssistance(request.roadsideAssistance());
        policy.setPaymentFrequency(request.paymentFrequency());

        policy.setVehicleManufacturer(request.vehicleManufacturer());
        policy.setVehicleModel(request.vehicleModel());
        policy.setLicensePlate(request.licensePlate());
        policy.setFirstRegistrationYear(request.firstRegistrationYear());

        policy.setStartDate(request.startDate());
        policy.setEndDate(request.startDate().plusYears(1).minusDays(1));

        policy.setAnnualPremium(quote.annualPremium());
        policy.setInstallmentAmount(quote.installmentAmount());

        return toResponse(policyRepository.save(policy));
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        InsurancePolicy policy = findPolicy(id);

        policyRepository.delete(policy);
    }

    // PDF EXPORT: VOLLSTÄNDIGE ENTITY LADEN
    @Transactional(readOnly = true)
    public InsurancePolicy getPolicyEntityById(Long id) {

        return findPolicy(id);
    }

    // HELPER: VERTRAG FINDEN
    private InsurancePolicy findPolicy(Long id) {

        return policyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Insurance policy not found"
                ));
    }

    // ENTITY -> RESPONSE DTO
    private InsurancePolicyResponse toResponse(InsurancePolicy policy) {

        return new InsurancePolicyResponse(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getCustomer().getId(),
                policy.getInsuranceType(),
                policy.isRoadsideAssistance(),
                policy.getPaymentFrequency(),
                policy.getVehicleManufacturer(),
                policy.getVehicleModel(),
                policy.getLicensePlate(),
                policy.getFirstRegistrationYear(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getAnnualPremium(),
                policy.getInstallmentAmount(),
                policy.getStatus()
        );
    }
}