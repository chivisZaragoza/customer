package com.example.customer.service;

import com.example.customer.entities.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<Customer> findAll();
    Optional<Customer> findById(Long id);
    Customer save(Customer customer);
}
