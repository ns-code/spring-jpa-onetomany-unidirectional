package com.example.customercontactapp.web.controller;

import com.example.customercontactapp.entity.Contact;
import com.example.customercontactapp.entity.ContactType;
import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.model.CustomerDTO;
import com.example.customercontactapp.repository.ContactRepository;
import com.example.customercontactapp.repository.CustomerRepository;
import com.example.customercontactapp.service.ContactService;
import com.example.customercontactapp.util.ContactMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {com.example.customercontactapp.config.AppConfig.class}
)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"test"})
class ContactControllerIntegrationTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private ContactService contactService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        customerRepository.deleteAll();
    }    

    @Test
    void testAddContact() throws Exception {
        // arrange
        ContactDTO contactDTO = new ContactDTO(null, "123 123 1234", null, ContactType.PHONE);
        List<ContactDTO> contactDTOs = List.of(contactDTO);
        CustomerDTO customerDTO = new CustomerDTO(null, "John Doe", contactDTOs);
        MvcTestResult result = createCustomer(customerDTO);
        Long customerId = Long.parseLong(result.getResponse().getContentAsString());

        // act
        contactDTO = new ContactDTO(null, "me@myco.com", customerId, ContactType.EMAIL);
        contactDTO.setCustomerId(customerId);
        result = createContact(customerId, contactDTO);

        // assert
        assertThat(result.getResponse().getStatus()).isEqualTo(201);                
        Long newContactId = Long.parseLong(result.getResponse().getContentAsString());
        assertThat(newContactId).isGreaterThan(0);
    }
    
    private MvcTestResult createCustomer(CustomerDTO customerDTO) throws Exception {
         return mockMvc.post().uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO))
                .exchange();
    }    

    private MvcTestResult createContact(Long customerId, ContactDTO contactDTO) throws Exception {
         return mockMvc.post().uri("/api/contacts" + "/" + customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDTO))
                .exchange();
    }
    
    @Test
    void testUpdateContact() throws Exception {
        // arrange
        ContactDTO contactDTO = new ContactDTO(null, "123 123 1234", null, ContactType.PHONE);
        List<ContactDTO> contactDTOs = List.of(contactDTO);
        CustomerDTO customerDTO = new CustomerDTO(null, "John Doe", contactDTOs);
        MvcTestResult createResult = createCustomer(customerDTO);
        // first create a customer with a contact
        Long newCustomerId = Long.parseLong(createResult.getResponse().getContentAsString());

        // act
        List<Contact> contacts = contactRepository.findByCustomerId(newCustomerId);
        contactDTO = contactMapper.toDto(contacts.get(0));
        // update the contact info
        contactDTO.setContactInfo("99-999-9999");
        MvcTestResult result = mockMvc.put().uri("/api/contacts/"+contactDTO.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDTO))
                .exchange();

        // assert
        int status = result.getResponse().getStatus(); 
        assertThat(status).isEqualTo(204);
    }
    
}