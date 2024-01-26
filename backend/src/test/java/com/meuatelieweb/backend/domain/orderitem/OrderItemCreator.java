package com.meuatelieweb.backend.domain.orderitem;


import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.order.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderItemCreator {

    public static OrderItem createValidAdjustOrderItem() {

        UUID id = UUID.randomUUID();
        String title = "Blusa de Seda";
        String description = null;
        Double cost = 13.90;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        LocalDateTime deliveredAt= null;
        Boolean isActive = true;
        List<CustomerAdjust> customerAdjustments = List.of();
        Order order = new Order();

        OrderItem adjustItem = new AdjustOrderItem();
        adjustItem.setId(id);
        adjustItem.setTitle(title);
        adjustItem.setDescription(description);
        adjustItem.setCost(cost);
        adjustItem.setCreatedAt(createdAt);
        adjustItem.setDueDate(dueDate);
        adjustItem.setDeliveredAt(deliveredAt);
        adjustItem.setIsActive(isActive);
        ((AdjustOrderItem) adjustItem).setCustomerAdjustments(customerAdjustments);
        adjustItem.setOrder(order);

        return adjustItem;
    }

    public static OrderItem createValidTailoredOrderItem() {

        UUID id = UUID.randomUUID();
        String title = "Vestido de Festa";
        String description = null;
        Double cost = 350.50;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(30);
        LocalDateTime deliveredAt= null;
        Boolean isActive = true;
        List<CustomerMeasure> customerMeasures = List.of();
        Order order = new Order();

        OrderItem tailoredItem = new TailoredOrderItem();
        tailoredItem.setId(id);
        tailoredItem.setTitle(title);
        tailoredItem.setDescription(description);
        tailoredItem.setCost(cost);
        tailoredItem.setCreatedAt(createdAt);
        tailoredItem.setDueDate(dueDate);
        tailoredItem.setDeliveredAt(deliveredAt);
        tailoredItem.setIsActive(isActive);
        ((TailoredOrderItem) tailoredItem).setCustomerMeasures(customerMeasures);
        tailoredItem.setOrder(order);

        return tailoredItem;
    }

    public static List<OrderItem> createValidOrderItemsList() {
        return List.of(
                createValidAdjustOrderItem(),
                createValidTailoredOrderItem()
        );
    }
}
