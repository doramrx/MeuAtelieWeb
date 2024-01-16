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

    @NotNull(message = "The given cost cannot be empty")
    @Positive(message = "The given cost must be higher than 0")
    @Digits(integer = 5, fraction = 2, message = "The cost must have up to 5 digits in total, with 2 decimals")
    private Double cost;
}
