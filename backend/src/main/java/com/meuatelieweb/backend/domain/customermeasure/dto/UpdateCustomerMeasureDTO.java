package com.meuatelieweb.backend.domain.customermeasure.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCustomerMeasureDTO {

    @Positive(message = "The given measure value cannot be lesser than 0.01 cm")
    @Digits(integer = 3, fraction = 2, message = "The measure value must have up to 3 digits in total, with 2 decimals")
    private Double measurementValue;
}

