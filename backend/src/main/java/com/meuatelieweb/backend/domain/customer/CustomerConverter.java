package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter {

    public CustomerDTO toCustomerDTO(Customer customer) {
        return new CustomerDTO()
                .builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .isActive(customer.getIsActive())
                .build();
    }
}
