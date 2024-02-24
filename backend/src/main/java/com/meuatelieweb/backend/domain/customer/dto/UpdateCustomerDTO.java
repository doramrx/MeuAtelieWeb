package com.meuatelieweb.backend.domain.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerDTO {

    @NotBlank(message = "{shared.error.emptyName}")
    private String name;

    @Pattern(regexp = "\\d{11}", message = "{customer.error.invalidPhonePattern}")
    private String phone;
}
