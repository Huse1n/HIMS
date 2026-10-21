package com.hims.customer.insurance;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class InsurancePolicyController {

    private final InsurancePolicyService policyService;
    private final InsurancePolicyPdfService pdfService;

    public InsurancePolicyController(
            InsurancePolicyService policyService,
            InsurancePolicyPdfService pdfService
    ) {
        this.policyService = policyService;
        this.pdfService = pdfService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<InsurancePolicyResponse> create(
            @Valid @RequestBody InsurancePolicyRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(policyService.create(request));
    }

    // READ ALL
    @GetMapping
    public List<InsurancePolicyResponse> findAll() {
        return policyService.findAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public InsurancePolicyResponse findById(@PathVariable Long id) {
        return policyService.findById(id);
    }

    // READ BY CUSTOMER
    @GetMapping("/customer/{customerId}")
    public List<InsurancePolicyResponse> findByCustomer(
            @PathVariable Long customerId
    ) {
        return policyService.findByCustomer(customerId);
    }

    // UPDATE
    @PutMapping("/{id}")
    public InsurancePolicyResponse update(
            @PathVariable Long id,
            @Valid @RequestBody InsurancePolicyRequest request
    ) {
        return policyService.update(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // PDF EXPORT
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPolicyPdf(
            @PathVariable Long id
    ) {

        InsurancePolicy policy = policyService.getPolicyEntityById(id);

        byte[] pdf = pdfService.generatePdf(policy);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + policy.getPolicyNumber()
                                + ".pdf\""
                )
                .body(pdf);
    }
}