package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.CustomerConverter;
import com.meuatelieweb.backend.domain.order.dto.OrderDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderItemConverter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderConverter {
    @Autowired
    private CustomerConverter customerConverter;

    @Autowired
    private OrderItemConverter orderItemConverter;

    public OrderDTO toOrderDTO(@NonNull Order order) {
        return new OrderDTO()
                .builder()
                .id(order.getId())
                .cost(order.getCost())
                .dueDate(order.getDueDate())
                .createdAt(order.getCreatedAt())
                .deliveredAt(order.getDeliveredAt())
                .customer(this.customerConverter.toCustomerDTO(order.getCustomer()))
                .orderItems(order.getOrderItems().stream()
                        .map(this.orderItemConverter::toOrderItemDTO)
                        .collect(Collectors.toSet())
                )
                .isActive(order.getIsActive())
                .build();
    }
}
