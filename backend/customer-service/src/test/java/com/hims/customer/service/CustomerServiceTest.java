package com.hims.customer.service;

import com.hims.customer.dto.CustomerRequest;
import com.hims.customer.dto.CustomerResponse;
import com.hims.customer.entity.Customer;
import com.hims.customer.exception.CustomerNotFoundException;
import com.hims.customer.exception.DuplicateEmailException;
import com.hims.customer.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // TEST 1: Customer successfully created

    @Test
    void shouldCreateCustomerSuccessfully() {

        // Arrange
        CustomerRequest request = new CustomerRequest(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        Customer savedCustomer = new Customer(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        // Act
        CustomerResponse response =
                customerService.createCustomer(request);

        // Assert
        assertNotNull(response);
        assertEquals("Max", response.firstName());
        assertEquals("Mustermann", response.lastName());
        assertEquals("max@example.com", response.email());

        verify(customerRepository).save(any(Customer.class));
    }

    // TEST 2: Duplicate email

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        CustomerRequest request = new CustomerRequest(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerRepository.existsByEmail(request.email()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateEmailException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    // TEST 3: Customer not found

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        // Arrange
        Long customerId = 999L;

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById(customerId)
        );
    }

    // TEST 4: Customer successfully found by ID

    @Test
    void shouldGetCustomerByIdSuccessfully() {

        // Arrange
        Long customerId = 1L;

        Customer customer = new Customer(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        // Act
        CustomerResponse response =
                customerService.getCustomerById(customerId);

        // Assert
        assertNotNull(response);
        assertEquals("Max", response.firstName());
        assertEquals("Mustermann", response.lastName());
        assertEquals("max@example.com", response.email());

        verify(customerRepository).findById(customerId);
    }

    // TEST 5: Get all customers successfully

    @Test
    void shouldGetAllCustomersSuccessfully() {

        // Arrange
        Customer customer1 = new Customer(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        Customer customer2 = new Customer(
                "Anna",
                "Schmidt",
                "anna@example.com"
        );

        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));

        // Act
        List<CustomerResponse> responses =
                customerService.getAllCustomers();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals("Max", responses.get(0).firstName());
        assertEquals("Anna", responses.get(1).firstName());

        verify(customerRepository).findAll();
    }

    // TEST 6: Customer successfully updated

    @Test
    void shouldUpdateCustomerSuccessfully() {

        // Arrange
        Long customerId = 1L;

        Customer existingCustomer = new Customer(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        CustomerRequest request = new CustomerRequest(
                "Alexander",
                "Schmidt",
                "alex@example.com"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CustomerResponse response =
                customerService.updateCustomer(customerId, request);

        // Assert
        assertNotNull(response);

        assertEquals("Alexander", response.firstName());
        assertEquals("Schmidt", response.lastName());
        assertEquals("alex@example.com", response.email());

        verify(customerRepository).findById(customerId);
        verify(customerRepository).save(any(Customer.class));
    }

    // TEST 7: Update customer - Customer not found

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCustomer() {

        // Arrange
        Long customerId = 999L;

        CustomerRequest request = new CustomerRequest(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(customerId, request)
        );

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    // TEST 8: Update customer - Duplicate email

    @Test
    void shouldThrowExceptionWhenUpdatingWithDuplicateEmail() {

        // Arrange
        Long customerId = 1L;

        Customer existingCustomer = new Customer(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        CustomerRequest request = new CustomerRequest(
                "Max",
                "Mustermann",
                "anna@example.com"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(existingCustomer));

        when(customerRepository.existsByEmail(request.email()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateEmailException.class,
                () -> customerService.updateCustomer(customerId, request)
        );

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    // TEST 9: Delete customer successfully

    @Test
    void shouldDeleteCustomerSuccessfully() {

        // Arrange
        Long customerId = 1L;

        Customer customer = new Customer(
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        // Act
        customerService.deleteCustomer(customerId);

        // Assert
        verify(customerRepository).findById(customerId);
        verify(customerRepository).delete(customer);
    }

    // TEST 10: Delete customer - Customer not found

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCustomer() {

        // Arrange
        Long customerId = 999L;

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.deleteCustomer(customerId)
        );

        verify(customerRepository, never())
                .delete(any(Customer.class));
    }
}