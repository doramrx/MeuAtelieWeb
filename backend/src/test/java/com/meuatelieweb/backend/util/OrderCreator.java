package com.meuatelieweb.backend.util;

import com.meuatelieweb.backend.domain.order.Order;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.meuatelieweb.backend.domain.orderitem.OrderItemCreator.createValidOrderItemsList;
import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomer;

public class OrderCreator {

    public static Order createValidOrder() {
        return Order.builder()
                .id(UUID.randomUUID())
                .orderNumber(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .finishedAt(null)
                .customer(createValidCustomer())
//                .orderItems(createValidOrderItemsList())
                .isActive(true)
                .build();
    }
}
