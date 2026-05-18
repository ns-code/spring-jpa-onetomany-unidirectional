package com.example.customercontactapp.util;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.customercontactapp.entity.Contact;
import com.example.customercontactapp.entity.Customer;
import com.example.customercontactapp.model.ContactDTO;
import com.example.customercontactapp.model.CustomerDTO;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "customer.id", target = "customerId")
    ContactDTO toDto(Contact entity);

    @Mapping(source = "customerId", target = "customer.id")
    Contact toEntity(ContactDTO dto);

    List<ContactDTO> toDtoList(List<Contact> entities);

    List<Contact> toEntityList(List<ContactDTO> dtos);     
}

