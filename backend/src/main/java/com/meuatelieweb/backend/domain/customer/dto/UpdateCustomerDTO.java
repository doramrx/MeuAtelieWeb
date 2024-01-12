package com.meuatelieweb.backend.domain.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerDTO {

    @NotBlank(message = "The given name cannot be empty")
    private String name;

    @Size(min = 11, max = 11, message = "The given phone number is not valid (invalid size)")
    @Pattern(regexp = "\\d{11}", message = "The given phone number is not valid (invalid format)")
    private String phone;
}
