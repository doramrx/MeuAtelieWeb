package com.meuatelieweb.backend.domain.measure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeasureDTO {
    private UUID id;
    private String name;
    private Boolean isActive;
}
