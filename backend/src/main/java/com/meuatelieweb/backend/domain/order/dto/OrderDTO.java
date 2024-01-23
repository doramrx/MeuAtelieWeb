package com.meuatelieweb.backend.domain.order.dto;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderType;
import com.meuatelieweb.backend.domain.orderitem.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private UUID id;
    private Integer orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime finishedAt;
    private CustomerDTO customer;
    private Set<OrderItemDTO> orderItems;
    private Boolean isActive;
}
