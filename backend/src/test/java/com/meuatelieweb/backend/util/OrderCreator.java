package com.meuatelieweb.backend.util;

import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.UpdateOrderDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomer;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidOrderItemsList;

public class OrderCreator {

    public static Order createValidOrder() {
        return Order.builder()
                .id(UUID.randomUUID())
                .orderNumber(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .finishedAt(null)
                .customer(createValidCustomer())
                .orderItems(createValidOrderItemsList())
                .isActive(true)
                .build();
    }

    public static SaveOrderDTO createValidSaveOrderDTO(UUID customerId, Set<SaveOrderItemDTO> items) {
        return SaveOrderDTO.builder()
                .customerId(customerId)
                .items(items)
                .build();
    }

    public static UpdateOrderDTO createValidUpdateOrderDTO(UUID customerId) {
        return UpdateOrderDTO.builder()
                .customerId(customerId)
                .build();
    }
}
