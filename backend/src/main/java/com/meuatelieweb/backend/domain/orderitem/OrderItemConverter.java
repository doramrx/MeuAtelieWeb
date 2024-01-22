package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.orderitem.dto.OrderItemDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter {

    public OrderItemDTO toOrderItemDTO(@NonNull OrderItem orderItem) {
        return new OrderItemDTO()
                .builder()
                .id(orderItem.getId())
                .title(orderItem.getTitle())
                .description(orderItem.getDescription())
                .cost(orderItem.getCost())
                .isActive(orderItem.getIsActive())
                .build();
    }
}
