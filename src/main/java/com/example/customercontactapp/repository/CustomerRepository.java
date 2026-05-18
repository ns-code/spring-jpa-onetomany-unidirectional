package com.example.customercontactapp.repository;

import com.example.customercontactapp.entity.Customer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    public List<Customer> findAllByOrderByName();

    @Modifying
    @Query("delete from Contact c where c.customer.id = :id")
    void deleteByCustomerId(Long id);

}