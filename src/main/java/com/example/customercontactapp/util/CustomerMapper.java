package com.example.customercontactapp.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.customercontactapp.entity.Customer;
import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.model.CustomerDTO;

@Mapper(componentModel = "spring", uses = ContactMapper.class)
public interface CustomerMapper {

    // ENTITY → DTO
    @Mapping(source = "customer.id", target = "id")
    @Mapping(source = "customer.name", target = "name")
    @Mapping(source = "contacts", target = "contacts")
    CustomerDTO toDTO(Customer customer, List<ContactDTO> contacts);

    // DTO → ENTITY 
    Customer toEntity(CustomerDTO dto);

    // LIST MAPPINGS
    List<CustomerDTO> toDtoList(List<Customer> entities);

    List<Customer> toEntityList(List<CustomerDTO> dtos);    
}


