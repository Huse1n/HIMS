package com.hims.customer.integration;
import java.util.List;

import com.hims.customer.dto.CustomerRequest;
import com.hims.customer.dto.CustomerResponse;
import com.hims.customer.repository.CustomerRepository;
import com.hims.customer.service.CustomerService;
import com.hims.customer.exception.DuplicateEmailException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class CustomerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    // TEST 1: Create customer and save to real PostgreSQL database

    @Test
    void shouldCreateCustomerInDatabase() {

        // Arrange
        CustomerRequest request = new CustomerRequest(
                "Max",
                "Mustermann",
                "integration@example.com"
        );

        // Act
        CustomerResponse response =
                customerService.createCustomer(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.id());

        assertTrue(
                customerRepository.existsByEmail("integration@example.com")
        );
    }

    // TEST 2: Get customer by ID from real database

    @Test
    void shouldGetCustomerByIdFromDatabase() {

        // Arrange
        CustomerRequest request = new CustomerRequest(
                "Anna",
                "Schmidt",
                "anna.integration@example.com"
        );

        CustomerResponse created =
                customerService.createCustomer(request);

        // Act
        CustomerResponse found =
                customerService.getCustomerById(created.id());

        // Assert
        assertNotNull(found);
        assertEquals(created.id(), found.id());
        assertEquals("Anna", found.firstName());
        assertEquals("anna.integration@example.com", found.email());
    }

    // TEST 3: Get all customers from real database

    @Test
    void shouldGetAllCustomersFromDatabase() {

        // Arrange
        customerService.createCustomer(
                new CustomerRequest(
                        "Max",
                        "Mustermann",
                        "max.list@example.com"
                )
        );

        customerService.createCustomer(
                new CustomerRequest(
                        "Anna",
                        "Schmidt",
                        "anna.list@example.com"
                )
        );

        // Act
        List<CustomerResponse> customers =
                customerService.getAllCustomers();

        // Assert
        assertTrue(customers.size() >= 2);

        assertTrue(
                customers.stream()
                        .anyMatch(c -> c.email().equals("max.list@example.com"))
        );

        assertTrue(
                customers.stream()
                        .anyMatch(c -> c.email().equals("anna.list@example.com"))
        );
    }

    // TEST 4: Update customer in real database

    @Test
    void shouldUpdateCustomerInDatabase() {

        // Arrange
        CustomerResponse created =
                customerService.createCustomer(
                        new CustomerRequest(
                                "Max",
                                "Mustermann",
                                "max.update@example.com"
                        )
                );

        CustomerRequest updateRequest = new CustomerRequest(
                "Alexander",
                "Schmidt",
                "alex.update@example.com"
        );

        // Act
        customerService.updateCustomer(
                created.id(),
                updateRequest
        );

        // Assert
        CustomerResponse updated =
                customerService.getCustomerById(created.id());

        assertEquals("Alexander", updated.firstName());
        assertEquals("Schmidt", updated.lastName());
        assertEquals("alex.update@example.com", updated.email());
    }

    // TEST 5: Delete customer from real database

    @Test
    void shouldDeleteCustomerFromDatabase() {

        // Arrange
        CustomerResponse created =
                customerService.createCustomer(
                        new CustomerRequest(
                                "Max",
                                "Mustermann",
                                "max.delete@example.com"
                        )
                );

        Long customerId = created.id();

        // Act
        customerService.deleteCustomer(customerId);

        // Assert
        assertFalse(customerRepository.existsById(customerId));
    }

    // TEST 6: Prevent duplicate email in real database

    @Test
    void shouldPreventDuplicateEmailInDatabase() {

        // Arrange
        CustomerRequest request = new CustomerRequest(
                "Max",
                "Mustermann",
                "duplicate.integration@example.com"
        );

        customerService.createCustomer(request);

        // Act & Assert
        assertThrows(
                DuplicateEmailException.class,
                () -> customerService.createCustomer(request)
        );
    }


}