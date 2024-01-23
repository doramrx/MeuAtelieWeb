package com.meuatelieweb.backend.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListOrderCustomerDTO {
    private String name;
    private String email;
}
