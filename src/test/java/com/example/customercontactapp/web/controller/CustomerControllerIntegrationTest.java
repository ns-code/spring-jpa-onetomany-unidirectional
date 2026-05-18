package com.example.customercontactapp.web.controller;

import com.example.customercontactapp.entity.*;
import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.model.CustomerDTO;
import com.example.customercontactapp.repository.ContactRepository;
import com.example.customercontactapp.repository.CustomerRepository;
import com.example.customercontactapp.service.CustomerService;
import com.example.customercontactapp.util.ContactMapper;
import com.example.customercontactapp.util.CustomerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"test"})
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ContactMapper contactMapper;
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
    void testCreateCustomer() throws Exception {
        // arrange
        ContactDTO contactDTO = new ContactDTO(null, "123 123 1234", null, ContactType.PHONE);
        List<ContactDTO> contactDTOs = List.of(contactDTO);
        CustomerDTO customerDTO = new CustomerDTO(null, "John Doe", contactDTOs);

        // act
        MvcTestResult result = createCustomer(customerDTO);

        // assert
        assertThat(result.getResponse().getStatus()).isEqualTo(201);                
        Long newCustomerId = Long.parseLong(result.getResponse().getContentAsString());
        assertThat(newCustomerId).isGreaterThan(0);
    }

    private MvcTestResult createCustomer(CustomerDTO customerDTO) throws Exception {
         return mockMvc.post().uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO))
                .exchange();
    }
        
    @Test
    void testUpdateCustomer() throws Exception {
        // arrange
        ContactDTO contactDTO = new ContactDTO(null, "123 123 1234", null, ContactType.PHONE);
        List<ContactDTO> contactDTOs = List.of(contactDTO);
        CustomerDTO customerDTO = new CustomerDTO(null, "John Doe", contactDTOs);
        MvcTestResult createResult = createCustomer(customerDTO);
        Long newCustomerId = Long.parseLong(createResult.getResponse().getContentAsString());

        // act
        MvcTestResult result = mockMvc.put().uri("/api/customers/"+newCustomerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO))
                .exchange();

        // assert
        int status = result.getResponse().getStatus(); 
        assertThat(status).isEqualTo(204);
    }

    @Test
    void testUpdateMultipleCustomerContacts() throws Exception {
        // arrange
        ContactDTO contactDTO = new ContactDTO(null, "123 123 1234", null, ContactType.PHONE);
        ContactDTO contactDTO2 = new ContactDTO(null, "myco@myco.com", null, ContactType.EMAIL);
        List<ContactDTO> contactDTOs = List.of(contactDTO, contactDTO2);
        CustomerDTO customerDTO = new CustomerDTO(null, "John Doe", contactDTOs);
        MvcTestResult createResult = createCustomer(customerDTO);
        Long newCustomerId = Long.parseLong(createResult.getResponse().getContentAsString());
        List<Contact> contacts = contactRepository.findByCustomerId(newCustomerId);
        List<ContactDTO> contactDTOsUpdated = List.of(contactMapper.toDto(contacts.get(0)));
        customerDTO = new CustomerDTO(newCustomerId, "John Doe", contactDTOsUpdated);

        // act
        MvcTestResult result = mockMvc.put().uri("/api/customers/"+newCustomerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO))
                .exchange();

        // assert
        int status = result.getResponse().getStatus(); 
        assertThat(status).isEqualTo(204);
        List<Contact> contacts2 = contactRepository.findByCustomerId(newCustomerId);
        assertThat(contacts2).hasSize(1);
    }    

/*     @Test
    void testGetAllCustomers() throws Exception {
        customerRepository.save(new Customer("John Doe"));
        customerRepository.save(new Customer("Jane Smith"));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

    @Test
    void testGetCustomerById() throws Exception {
        Customer customer = customerRepository.save(new Customer("John Doe"));

        mockMvc.perform(get("/api/customers/{id}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customer.getId()))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetCustomerById_NotFound() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", 999L))
                .andExpect(status().isNotFound());
    }


    @Test
    void testUpdateCustomer_NotFound() throws Exception {
        Customer customer = new Customer("John Doe");

        mockMvc.perform(put("/api/customers/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());
    }
 */
}