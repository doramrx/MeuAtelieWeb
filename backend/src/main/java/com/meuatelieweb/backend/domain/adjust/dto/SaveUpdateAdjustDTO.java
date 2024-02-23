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

    @NotBlank(message = "{shared.error.emptyName}")
    private String name;

    @Positive(message = "{shared.error.invalidCost}")
    @Digits(integer = 4, fraction = 2, message = "{shared.error.invalidFormatCost}")
    private Double cost;
}
