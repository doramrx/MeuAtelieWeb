package com.meuatelieweb.backend.domain.customermeasure.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveCustomerMeasureDTO {

    @NotNull(message = "The given id cannot be null")
    private UUID measurementId;

    @Positive(message = "The given measure value cannot be lesser than 0.01 cm")
    @Digits(integer = 3, fraction = 2, message = "The measure value must have up to 3 digits in total, with 2 decimals")
    private Double measurementValue;
}
