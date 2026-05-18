package com.example.customercontactapp.service;

import com.example.customercontactapp.entity.Contact;
import com.example.customercontactapp.entity.Customer;
import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.repository.ContactRepository;
import com.example.customercontactapp.repository.CustomerRepository;
import com.example.customercontactapp.util.ContactMapper;
import com.example.customercontactapp.web.error.NeedAtLeastOneContact;
import com.example.customercontactapp.web.error.NotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ContactService {
    
    private final ContactRepository contactRepository;
    private final CustomerRepository customerRepository;
    private final ContactMapper contactMapper;
    
    public ContactService(ContactRepository contactRepository, CustomerRepository customerRepository, ContactMapper contactMapper) {
        this.contactRepository = contactRepository;
        this.customerRepository = customerRepository;
        this.contactMapper = contactMapper;
    }
    
    public Long createContact(Long customerId, ContactDTO contactDTO) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + customerId));
        contactDTO.setCustomerId(customerId);
        Contact contact = contactMapper.toEntity(contactDTO);
        return contactRepository.save(contact).getId();
    }
    
    public void updateContact(Long customerId, ContactDTO contactDTO) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + customerId));
        Contact contact = contactRepository.findById(contactDTO.getId())
            .orElseThrow(() -> new NotFoundException("Contact not found with id: " + contactDTO.getId()));
        
        contact.setContactType(contactDTO.getContactType());
        contact.setContactInfo(contactDTO.getContactInfo());
        contactRepository.save(contact);
    }

    public List<ContactDTO> getAllContacts() {
        return contactMapper.toDtoList(contactRepository.findAll());
    }
    
    public Optional<Contact> getContact(Long id) {
        return contactRepository.findById(id);
    }
    
    public List<ContactDTO> getContactsForCustomer(Long customerId) {
        return contactMapper.toDtoList(contactRepository.findByCustomerId(customerId));
    }
    
    public void deleteContact(Long id) {
        contactRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Contact not found with id: " + id));
        List<Contact> contacts = contactRepository.findAllBySameCustomer(id);
        log.info(">> contacts size: {} {}", id, contacts.size());
        if (contacts.size() < 2) {
            throw new NeedAtLeastOneContact("Cannot delete, need at least one contact");
        }   
        contactRepository.deleteById(id);
    }    
}