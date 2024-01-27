package com.meuatelieweb.backend.util;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.ListOrderCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;

import java.util.UUID;

public class CustomerCreator {

    public static Customer createValidCustomer() {
        return Customer.builder()
                .id(UUID.randomUUID())
                .name("Ada Lovelace")
                .email("ada@lovelace.com")
                .phone("00100110100")
                .isActive(true)
                .build();
    }

    public static CustomerDTO createValidCustomerDTO(UUID id) {
        return CustomerDTO.builder()
                .id(id)
                .name("Ada Lovelace")
                .email("ada@lovelace.com")
                .phone("00100110100")
                .isActive(true)
                .build();
    }

    public static SaveCustomerDTO createValidSaveCustomerDTO() {
        return SaveCustomerDTO.builder()
                .name("Ada Lovelace")
                .email("ada@lovelace.com")
                .phone("00100110100")
                .build();
    }

    public static UpdateCustomerDTO createValidUpdateCustomerDTO() {
        return UpdateCustomerDTO.builder()
                .name("Ada Lovelace")
                .phone("00100110100")
                .build();
    }

    public static ListOrderCustomerDTO createValidListOrderCustomerDTO(Customer customer) {
        return ListOrderCustomerDTO.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }
}