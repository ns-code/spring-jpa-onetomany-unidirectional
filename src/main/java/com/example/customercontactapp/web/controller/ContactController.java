package com.example.customercontactapp.web.controller;

import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;    
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<Long> addContact(@PathVariable("customerId") Long customerId, @RequestBody ContactDTO contact) {
        Long createdContactId = contactService.createContact(customerId, contact);
        return new ResponseEntity<>(createdContactId, HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateContact(@PathVariable("customerId") Long customerId, @RequestBody ContactDTO contact) {
        contactService.updateContact(customerId, contact);
        return ResponseEntity.noContent().build();
    }    

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<ContactDTO>> getContactsForCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(contactService.getContactsForCustomer(customerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}