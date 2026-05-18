package com.example.customercontactapp.web.controller;

import com.example.customercontactapp.entity.ContactType;
import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.model.CustomerDTO;
import com.example.customercontactapp.repository.ContactRepository;
import com.example.customercontactapp.repository.CustomerRepository;
import com.example.customercontactapp.service.ContactService;
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
@SpringBootTest
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

/*     @Test
    void testCreateContact_CustomerNotFound() throws Exception {
        Contact contact = new Contact(null, ContactType.PHONE, "123-456-7890");

        mockMvc.perform(post("/api/contacts/customer/{customerId}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllContacts() throws Exception {
        Customer customer1 = customerRepository.save(new Customer("John Doe"));
        Customer customer2 = customerRepository.save(new Customer("Jane Smith"));
        
        customer1.addContact(new Contact(customer1.getId(),ContactType.PHONE, "111-111-1111"));
        customer2.addContact(new Contact(customer2.getId(),ContactType.EMAIL, "jane@example.com"));
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetContactById() throws Exception {
        Customer customer = new Customer("John Doe");
        customer.addContact(new Contact(customer.getId(),ContactType.PHONE, "123-456-7890"));
        Customer savedCustomer = customerRepository.save(customer);
        Contact savedContact = savedCustomer.getContacts().get(0);

        mockMvc.perform(get("/api/contacts/{id}", savedContact.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedContact.getId()))
                .andExpect(jsonPath("$.contactType").value("PHONE"))
                .andExpect(jsonPath("$.contactInfo").value("123-456-7890"));
    }

    @Test
    void testGetContactById_NotFound() throws Exception {
        mockMvc.perform(get("/api/contacts/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetContactsByCustomerId() throws Exception {
        Customer customer = new Customer("John Doe");
        customer.addContact(new Contact(customer.getId(),ContactType.PHONE, "111-111-1111"));
        customer.addContact(new Contact(customer.getId(),ContactType.EMAIL, "john@example.com"));
        customer.addContact(new Contact(customer.getId(),ContactType.HOME, "123 Main St"));
        Customer savedCustomer = customerRepository.save(customer);

        mockMvc.perform(get("/api/contacts/customer/{customerId}", savedCustomer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].contactType").value("PHONE"))
                .andExpect(jsonPath("$[1].contactType").value("EMAIL"))
                .andExpect(jsonPath("$[2].contactType").value("HOME"));
    }

    @Test
    void testGetContactsByCustomerId_EmptyList() throws Exception {
        Customer customer = customerRepository.save(new Customer("John Doe"));

        mockMvc.perform(get("/api/contacts/customer/{customerId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testUpdateContact() throws Exception {
        Customer customer = new Customer("John Doe");
        customer.addContact(new Contact(customer.getId(),ContactType.PHONE, "111-111-1111"));
        Customer savedCustomer = customerRepository.save(customer);
        Contact savedContact = savedCustomer.getContacts().get(0);

        Contact updatedContact = new Contact(customer.getId(),ContactType.EMAIL, "updated@example.com");

        mockMvc.perform(put("/api/contacts/{id}", savedContact.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedContact)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedContact.getId()))
                .andExpect(jsonPath("$.contactType").value("EMAIL"))
                .andExpect(jsonPath("$.contactInfo").value("updated@example.com"));
    }

    @Test
    void testUpdateContact_NotFound() throws Exception {
        Contact contact = new Contact(null,ContactType.PHONE, "123-456-7890");

        mockMvc.perform(put("/api/contacts/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contact)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteContact() throws Exception {
        Customer customer = new Customer("John Doe");
        customer.addContact(new Contact(customer.getId(),ContactType.PHONE, "111-111-1111"));
        Customer savedCustomer = customerRepository.save(customer);
        Contact savedContact = savedCustomer.getContacts().get(0);

        mockMvc.perform(delete("/api/contacts/{id}", savedContact.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/contacts/{id}", savedContact.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMultipleContactTypes() throws Exception {
        Customer customer = customerRepository.save(new Customer("John Doe"));

        Contact phoneContact = new Contact(customer.getId(),ContactType.PHONE, "123-456-7890");
        Contact homeContact = new Contact(customer.getId(),ContactType.HOME, "123 Main St");
        Contact emailContact = new Contact(customer.getId(),ContactType.EMAIL, "john@example.com");

        mockMvc.perform(post("/api/contacts/customer/{customerId}", customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(phoneContact)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactType").value("PHONE"));

        mockMvc.perform(post("/api/contacts/customer/{customerId}", customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(homeContact)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactType").value("HOME"));

        mockMvc.perform(post("/api/contacts/customer/{customerId}", customer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailContact)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactType").value("EMAIL"));

        mockMvc.perform(get("/api/contacts/customer/{customerId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
 */
}