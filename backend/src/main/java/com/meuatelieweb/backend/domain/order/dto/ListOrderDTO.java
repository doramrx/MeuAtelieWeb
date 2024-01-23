package com.meuatelieweb.backend.domain.order.dto;

import com.meuatelieweb.backend.domain.customer.dto.ListOrderCustomerDTO;
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
public class ListOrderDTO {
    private UUID id;
    private Integer orderNumber;
    private Double cost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime finishedAt;
    private ListOrderCustomerDTO customer;
    private Boolean isActive;
}
