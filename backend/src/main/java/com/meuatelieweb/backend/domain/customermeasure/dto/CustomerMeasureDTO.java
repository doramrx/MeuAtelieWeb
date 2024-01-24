package com.meuatelieweb.backend.domain.customermeasure.dto;

import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerMeasureDTO {
    private UUID id;
    private MeasureDTO measure;
    private Double measurementValue;
    private Boolean isActive;
}
