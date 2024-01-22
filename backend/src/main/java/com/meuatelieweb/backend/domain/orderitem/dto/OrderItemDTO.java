package com.meuatelieweb.backend.domain.orderitem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    private UUID id;
    private String title;
    private String description;
    private Double cost;
    private Boolean isActive;
}
