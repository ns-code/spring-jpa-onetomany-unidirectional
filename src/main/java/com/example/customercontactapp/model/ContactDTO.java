package com.example.customercontactapp.model;

import com.example.customercontactapp.entity.ContactType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    private Long id;
    private String contactInfo;
    private Long customerId;  
    private ContactType contactType;  
 }
