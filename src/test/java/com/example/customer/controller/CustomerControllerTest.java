package com.example.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.customer.entities.Customer;
import com.example.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCustomersReturnsList() throws Exception {
        Customer customer = new Customer(1L, "Alice", "alice@example.com", LocalDate.now());
        when(customerService.findAll()).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/customer/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void getCustomerByIdWhenPresentReturnsCustomer() throws Exception {
        Customer customer = new Customer(2L, "Bob", "bob@example.com", LocalDate.now());
        when(customerService.findById(2L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customer/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void getCustomerByIdWhenMissingReturnsNotFound() throws Exception {
        when(customerService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customer/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @SuppressWarnings("null")
    void createCustomerReturnsCreatedCustomer() throws Exception {
        Customer input = new Customer(null, "Carol", "carol@example.com", null);
        Customer saved = new Customer(10L, "Carol", "carol@example.com", LocalDate.now());
        when(customerService.save(any(Customer.class))).thenReturn(saved);

        mockMvc.perform(post("/api/customer")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Carol"));
    }
}
