package com.meuatelieweb.backend.domain.customer.dto;

import jakarta.validation.constraints.Email;
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
public class SaveCustomerDTO {

    @NotBlank(message = "{customer.error.emptyName}")
    private String name;

    @NotBlank(message = "{customer.error.emptyEmail}")
    @Email(message = "{customer.error.invalidEmailPattern}")
    private String email;

    @Pattern(regexp = "\\d{11}", message = "{customer.error.invalidPhonePattern}")
    private String phone;
}
