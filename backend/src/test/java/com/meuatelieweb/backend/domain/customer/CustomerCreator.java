package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;

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

    public static CustomerDTO createValidCustomerDTO(UUID uuid) {
        return CustomerDTO.builder()
                .id(uuid)
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
}