package com.example.customercontactapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "customer_id", nullable = false)
    // private List<Contact> contacts = new ArrayList<>();

    // public List<Contact> getContacts() {
    //     return contacts;
    // }

    // public void setContacts(List<Contact> contacts) {
    //     this.contacts = contacts;
    // }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Customer() {
    }

    public Customer(String name) {
        this.name = name;
    }

    // public void addContact(Contact contact) {
    //     this.contacts.add(contact);
    // }    

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Customer other))
            return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        // Ref: https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}