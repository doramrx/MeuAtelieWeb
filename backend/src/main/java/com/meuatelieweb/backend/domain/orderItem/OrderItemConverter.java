package com.meuatelieweb.backend.domain.orderItem;

import com.meuatelieweb.backend.domain.orderItem.dto.OrderItemDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {
    public OrderItemDTO toOrderItemDTO(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getIsActive()
        );
    }

}
