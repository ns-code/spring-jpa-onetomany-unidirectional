package com.example.customercontactapp.service;

import com.example.customercontactapp.entity.Contact;
import com.example.customercontactapp.entity.Customer;
import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.model.CustomerDTO;
import com.example.customercontactapp.repository.ContactRepository;
import com.example.customercontactapp.repository.CustomerRepository;
import com.example.customercontactapp.util.ContactMapper;
import com.example.customercontactapp.util.CustomerMapper;
import com.example.customercontactapp.web.error.NotFoundException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final CustomerMapper customerMapper;
    private final ContactMapper contactMapper;

    public CustomerService(CustomerRepository customerRepository, ContactRepository contactRepository,
            CustomerMapper customerMapper, ContactMapper contactMapper) {
        this.customerRepository = customerRepository;
        this.contactRepository = contactRepository;
        this.customerMapper = customerMapper;
        this.contactMapper = contactMapper;
    }

    public Long createCustomer(CustomerDTO customer) {
        Customer customerEntity = customerMapper.toEntity(customer);
        final Customer savedCustomer = customerRepository.save(customerEntity);

        if (customer.getContacts() != null) {
            customer.getContacts().forEach(contact -> contact.setCustomerId(savedCustomer.getId()));
            contactRepository.saveAll(contactMapper.toEntityList(customer.getContacts()));
        }
        return savedCustomer.getId();
    }

    /**
     * If incoming customerId not found then throw exception
     * If found then transform dto to entity, set entityId and save
     * If incoming customer has one or more contacts
     * then check if contactId is null or not found in db then save as new contact
     * 
     * @param id
     * @param customerDTO
     */
    @Transactional
    public void updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        customerDTO.setId(id);
        Customer customerEntity = customerMapper.toEntity(customerDTO);
        customerRepository.save(customerEntity);

        if (customerDTO.getContacts() != null) {
            List<Long> incomingContactIds = customerDTO.getContacts().stream()
                    .filter(contact -> contact.getId() != null)
                    .map(ContactDTO::getId)
                    .toList();
            List<Contact> existingContacts = contactRepository.findByCustomerId(id);
            List<Long> existingContactIds = existingContacts.stream().map(Contact::getId).toList();

            existingContactIds.stream().forEach(contactId -> {
                if (!incomingContactIds.contains(contactId)) {
                    contactRepository.deleteById(contactId);
                    log.info("Deleting contact with id: {}", contactId);
                }
            });
            customerDTO.getContacts().forEach(contact -> {
                if (contact.getId() == null || existingContactIds.contains(contact.getId())) {
                    Contact contactEntity = contactMapper.toEntity(contact);
                    contactEntity.setCustomer(customer);
                    contactRepository.save(contactEntity);
                } else {
                    contactRepository.deleteById(contact.getId());
                    log.info("Updating existing contact with id: {}", contact.getId());
                }
            });
        }
    }

    public CustomerDTO getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));

        List<ContactDTO> contactDTOs = contactMapper.toDtoList(contactRepository.findByCustomerId(id));
        CustomerDTO customerDTO = customerMapper.toDTO(customer);
        customerDTO.setContacts(contactDTOs);
        customerDTO.setContacts(contactDTOs);
        return customerDTO;
    }

    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> customerDTOs = customerMapper.toDtoList(customerRepository.findAllByOrderByName());
        customerDTOs.forEach(customerDTO -> {
            List<ContactDTO> contactDTOs = contactMapper
                    .toDtoList(contactRepository.findByCustomerId(customerDTO.getId()));
            customerDTO.setContacts(contactDTOs);
        });
        return customerDTOs;
    }

    public void deleteCustomer(Long id) {
        // Optional<Customer> customer =
        customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        contactRepository.deleteByCustomerId(id);
        customerRepository.deleteById(id);
    }
}