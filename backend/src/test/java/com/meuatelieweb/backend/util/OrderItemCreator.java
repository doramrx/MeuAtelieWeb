package com.meuatelieweb.backend.util;


import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.orderitem.AdjustOrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderType;
import com.meuatelieweb.backend.domain.orderitem.TailoredOrderItem;
import com.meuatelieweb.backend.domain.orderitem.dto.OrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.UpdateOrderItemDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.meuatelieweb.backend.util.CustomerAdjustCreator.createValidSaveCustomerAdjustDTO;
import static com.meuatelieweb.backend.util.CustomerMeasureCreator.createValidSaveCustomerMeasureDTO;

public class OrderItemCreator {

    public static OrderItem createValidOrderItem() {

        UUID id = UUID.randomUUID();
        String title = "Blusa de Seda";
        String description = null;
        Double cost = 13.90;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        LocalDateTime deliveredAt = null;
        Boolean isActive = true;

        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setTitle(title);
        orderItem.setDescription(description);
        orderItem.setCost(cost);
        orderItem.setCreatedAt(createdAt);
        orderItem.setDueDate(dueDate);
        orderItem.setDeliveredAt(deliveredAt);
        orderItem.setIsActive(isActive);

        return orderItem;
    }

    public static OrderItem createValidAdjustOrderItem() {

        UUID id = UUID.randomUUID();
        String title = "Blusa de Seda";
        String description = null;
        Double cost = 13.90;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        LocalDateTime deliveredAt = null;
        Boolean isActive = true;
        List<CustomerAdjust> customerAdjustments = List.of();

        AdjustOrderItem adjustItem = new AdjustOrderItem();
        adjustItem.setId(id);
        adjustItem.setTitle(title);
        adjustItem.setDescription(description);
        adjustItem.setCost(cost);
        adjustItem.setCreatedAt(createdAt);
        adjustItem.setDueDate(dueDate);
        adjustItem.setDeliveredAt(deliveredAt);
        adjustItem.setIsActive(isActive);
        adjustItem.setCustomerAdjustments(customerAdjustments);

        return adjustItem;
    }

    public static OrderItem createValidTailoredOrderItem() {

        UUID id = UUID.randomUUID();
        String title = "Vestido de Festa";
        String description = null;
        Double cost = 350.50;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(30);
        LocalDateTime deliveredAt = null;
        Boolean isActive = true;
        List<CustomerMeasure> customerMeasures = List.of();

        TailoredOrderItem tailoredItem = new TailoredOrderItem();
        tailoredItem.setId(id);
        tailoredItem.setTitle(title);
        tailoredItem.setDescription(description);
        tailoredItem.setCost(cost);
        tailoredItem.setCreatedAt(createdAt);
        tailoredItem.setDueDate(dueDate);
        tailoredItem.setDeliveredAt(deliveredAt);
        tailoredItem.setIsActive(isActive);
        tailoredItem.setCustomerMeasures(customerMeasures);

        return tailoredItem;
    }

    public static SaveOrderItemDTO createValidSaveOrderItemDTOTailored() {
        return SaveOrderItemDTO.builder()
                .type(OrderType.TAILORED)
                .title("Vestido de Festa")
                .description(null)
                .cost(350.50)
                .measures(List.of(createValidSaveCustomerMeasureDTO()))
                .dueDate(LocalDateTime.now().plusDays(30))
                .build();
    }

    public static SaveOrderItemDTO createValidSaveOrderItemDTOAdjust() {
        return SaveOrderItemDTO.builder()
                .type(OrderType.ADJUST)
                .title("Blusa de Seda")
                .description(null)
                .cost(13.90)
                .adjusts(List.of(createValidSaveCustomerAdjustDTO()))
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();
    }

    public static List<OrderItem> createValidOrderItemsList() {
        return List.of(
                createValidAdjustOrderItem(),
                createValidTailoredOrderItem()
        );
    }

    public static UpdateOrderItemDTO createValidUpdateOrderItemDTO() {
        return UpdateOrderItemDTO.builder()
                .title("Vestido")
                .description(null)
                .cost(100.0)
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();
    }

    public static OrderItemDTO createValidOrderItemDTO(UUID id) {
        return OrderItemDTO.builder()
                .id(id)
                .type(OrderType.ADJUST)
                .title("Blusa de Seda")
                .description(null)
                .cost(13.90)
                .createdAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(5))
                .deliveredAt(null)
                .isActive(true)
                .build();
    }
}
