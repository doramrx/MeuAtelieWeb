package com.meuatelieweb.backend.domain.order.dto;

import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveOrderDTO {
    private LocalDateTime dueDate;
    private UUID customerId;
    private Set<SaveOrderItemDTO> items;
}
