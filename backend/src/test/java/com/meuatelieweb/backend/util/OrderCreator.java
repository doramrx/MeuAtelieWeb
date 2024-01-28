package com.meuatelieweb.backend.util;

import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.order.dto.OrderDTO;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.UpdateOrderDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomer;
import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomerDTO;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidOrderItemsList;

public class OrderCreator {

    public static Order createValidOrder() {
        return Order.builder()
                .id(UUID.randomUUID())
                .orderNumber(1)
                .createdAt(LocalDateTime.of(2024, 01, 10, 10, 00, 00))
                .updatedAt(null)
                .finishedAt(LocalDateTime.of(2024, 12, 10, 10, 00, 00))
                .customer(createValidCustomer())
                .orderItems(createValidOrderItemsList())
                .isActive(true)
                .build();
    }

    public static OrderDTO createValidOrderDTO(UUID orderId, UUID customerId) {
        return OrderDTO.builder()
                .id(orderId)
                .orderNumber(1)
                .createdAt(LocalDateTime.of(2024, 01, 10, 10, 00, 00))
                .updatedAt(null)
                .finishedAt(LocalDateTime.of(2024, 12, 10, 10, 00, 00))
                .customer(createValidCustomerDTO(customerId))
                .orderItems(null)
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
