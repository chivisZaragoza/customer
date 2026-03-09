package com.example.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.customer.entities.Customer;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.service.impl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void findAllReturnsCustomersFromRepository() {
        Customer customer = new Customer(1L, "Alice", "alice@example.com", LocalDate.now());
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<Customer> result = customerService.findAll();

        assertThat(result).containsExactly(customer);
    }

    @Test
    void findByIdDelegatesToRepository() {
        Customer customer = new Customer(2L, "Bob", "bob@example.com", LocalDate.now());
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.findById(2L);

        assertThat(result).isPresent().contains(customer);
    }

    @Test
    @SuppressWarnings("null")
    void saveSetsCreateAtWhenMissing() {
        Customer customer = new Customer();
        customer.setName("Carol");
        customer.setEmail("carol@example.com");

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> Objects.requireNonNull(invocation.getArgument(0, Customer.class)));

        Customer saved = customerService.save(customer);

        assertThat(saved.getCreateAt()).isNotNull();
    }

    @Test
    @SuppressWarnings("null")
    void saveKeepsExistingCreateAt() {
        LocalDate creationDate = LocalDate.of(2024, 1, 1);
        Customer customer = new Customer();
        customer.setName("Dave");
        customer.setEmail("dave@example.com");
        customer.setCreateAt(creationDate);

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> Objects.requireNonNull(invocation.getArgument(0, Customer.class)));

        Customer saved = customerService.save(customer);

        assertThat(saved.getCreateAt()).isEqualTo(creationDate);
    }
}
