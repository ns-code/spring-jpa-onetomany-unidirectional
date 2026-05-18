package com.example.customercontactapp.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NeedAtLeastOneContact extends ResponseStatusException {
    public NeedAtLeastOneContact(String msg) {
        super(HttpStatus.BAD_REQUEST, msg);
    }
}