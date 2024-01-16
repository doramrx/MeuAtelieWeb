package com.meuatelieweb.backend.domain.measure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveUpdateMeasureDTO {
    @NotBlank(message = "The given name cannot be empty")
    private String name;
}
