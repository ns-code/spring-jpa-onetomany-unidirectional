package com.example.customercontactapp.repository;

import com.example.customercontactapp.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByCustomerId(Long customerId);

    @Transactional
    @Modifying
    @Query("delete from Contact c where c.customer.id = :id")
    void deleteByCustomerId(Long id);

    @Query("""
            select c
            from Contact c
            where c.customer.id = (
            select c2.customer.id
            from Contact c2
            where c2.id = :id
            )
            """)
    List<Contact> findAllBySameCustomer(@Param("id") Long id);

}