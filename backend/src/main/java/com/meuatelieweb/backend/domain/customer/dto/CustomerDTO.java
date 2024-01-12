package com.meuatelieweb.backend.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;
}
