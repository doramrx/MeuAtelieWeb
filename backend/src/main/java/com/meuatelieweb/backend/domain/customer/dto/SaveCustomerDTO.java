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

    @NotBlank(message = "The given name cannot be empty")
    private String name;

    @NotBlank(message = "The given email cannot be empty")
    @Email(message = "The given email is not valid")
    private String email;

    @Pattern(regexp = "\\d{11}", message = "The given phone is not valid")
    private String phone;
}
