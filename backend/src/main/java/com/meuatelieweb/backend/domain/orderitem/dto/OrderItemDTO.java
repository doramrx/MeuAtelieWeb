package com.meuatelieweb.backend.domain.orderitem.dto;

import com.meuatelieweb.backend.domain.orderitem.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    private UUID id;
    private OrderType type;
    private String title;
    private String description;
    private Double cost;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private LocalDateTime deliveredAt;
    private Boolean isActive;
}
