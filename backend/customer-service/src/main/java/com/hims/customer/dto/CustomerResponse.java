package com.hims.customer.dto;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}