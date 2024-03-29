package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.ListOrderCustomerDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter {

    public CustomerDTO toCustomerDTO(@NonNull Customer customer) {
        return new CustomerDTO()
                .builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .isActive(customer.getIsActive())
                .build();
    }

    public ListOrderCustomerDTO toListOrderCustomerDTO(@NonNull Customer customer) {
        return new ListOrderCustomerDTO()
                .builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }
}
