package com.meuatelieweb.backend.domain.orderItem.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    private UUID id;
    private String title;
    private String description;
    private Boolean isActive;
}
