package com.meuatelieweb.backend.domain.adjust.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdjustDTO {
    private UUID id;
    private String name;
    private Double cost;
    private Boolean isActive;
}
