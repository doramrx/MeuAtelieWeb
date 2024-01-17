package com.meuatelieweb.backend.domain.adjust.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveUpdateAdjustDTO {

    @NotBlank(message = "The given name cannot be empty")
    private String name;

    @Positive(message = "The given cost cannot be lesser than 0.01")
    @Digits(integer = 4, fraction = 2, message = "The cost must have up to 4 digits in total, with 2 decimals")
    private Double cost;
}
