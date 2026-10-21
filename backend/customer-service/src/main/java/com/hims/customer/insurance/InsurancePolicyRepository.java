package com.hims.customer.insurance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InsurancePolicyRepository
        extends JpaRepository<InsurancePolicy, Long> {

    List<InsurancePolicy> findByCustomerId(Long customerId);

    boolean existsByPolicyNumber(String policyNumber);
}