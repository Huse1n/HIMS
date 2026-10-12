package com.hims.customer.controller;

import com.hims.customer.dto.CustomerResponse;
import com.hims.customer.service.CustomerService;
import com.hims.customer.dto.CustomerRequest;
import com.hims.customer.exception.CustomerNotFoundException;
import com.hims.customer.exception.DuplicateEmailException;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    // TEST 1: Get all customers successfully

    @Test
    void shouldReturnAllCustomers() throws Exception {

        // Arrange
        CustomerResponse customer = new CustomerResponse(
                1L,
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerService.getAllCustomers())
                .thenReturn(List.of(customer));

        // Act & Assert
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Max"))
                .andExpect(jsonPath("$[0].lastName").value("Mustermann"))
                .andExpect(jsonPath("$[0].email").value("max@example.com"));
    }

    // TEST 2: GET Customer By ID

    @Test
    void shouldReturnCustomerById() throws Exception {

        CustomerResponse customer = new CustomerResponse(
                1L,
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerService.getCustomerById(1L))
                .thenReturn(customer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Max"))
                .andExpect(jsonPath("$.email").value("max@example.com"));
    }

    // TEST 3: Customer Not Found (404)

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {

        when(customerService.getCustomerById(999L))
                .thenThrow(new CustomerNotFoundException(999L));

        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound());
    }

    // TEST 4: Create Customer (201) Created

    @Test
    void shouldCreateCustomer() throws Exception {

        CustomerResponse response = new CustomerResponse(
                1L,
                "Max",
                "Mustermann",
                "max@example.com"
        );

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenReturn(response);

        String requestBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann",
                "email": "max@example.com"
            }
            """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Max"))
                .andExpect(jsonPath("$.email").value("max@example.com"));
    }

    // TEST 5: Duplicate Email (409) Conflict

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new DuplicateEmailException("max@example.com"));

        String requestBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann",
                "email": "max@example.com"
            }
            """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    // TEST 6: Update Customer (200) OK

    @Test
    void shouldUpdateCustomer() throws Exception {

        CustomerResponse response = new CustomerResponse(
                1L,
                "Alexander",
                "Schmidt",
                "alex@example.com"
        );

        when(customerService.updateCustomer(
                eq(1L),
                any(CustomerRequest.class)
        )).thenReturn(response);

        String requestBody = """
            {
                "firstName": "Alexander",
                "lastName": "Schmidt",
                "email": "alex@example.com"
            }
            """;

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alexander"))
                .andExpect(jsonPath("$.email").value("alex@example.com"));
    }

    // TEST 7: Delete Customer (204) No Content

    @Test
    void shouldDeleteCustomer() throws Exception {

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomer(1L);
    }


}