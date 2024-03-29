package com.meuatelieweb.backend.domain.order.dto;

import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveOrderDTO {
    private UUID customerId;
    private Set<SaveOrderItemDTO> items;
}
