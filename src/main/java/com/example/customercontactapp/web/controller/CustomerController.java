package com.example.customercontactapp.web.controller;

import com.example.customercontactapp.entity.Customer;
import com.example.customercontactapp.model.CustomerDTO;
import com.example.customercontactapp.service.CustomerService;
import com.example.customercontactapp.util.CustomerMapper;
import com.example.customercontactapp.web.error.NotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    private final CustomerService customerService;
    
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @PostMapping
    public ResponseEntity<Long> createCustomer(@RequestBody CustomerDTO customer) {
        Long createdCustomerId = customerService.createCustomer(customer);
        return new ResponseEntity<>(createdCustomerId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCustomer(@PathVariable("id") Long id, @RequestBody CustomerDTO customerDTO) {
        customerDTO.setId(id);
        customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable("id") Long id) {
        return customerService.getCustomer(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}