package com.meuatelieweb.backend.domain.orderitem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static com.meuatelieweb.backend.util.OrderItemCreator.createValidTailoredOrderItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for OrderItem Repository")
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("inactivateOrderItemById inactivates order item when successful")
    void inactivateOrderItemById_InactivatesOrderItem_WhenSuccessful() {
        OrderItem savedOrderItem = this. orderItemRepository.save(createValidTailoredOrderItem());

        orderItemRepository.inactivateOrderItemById(Set.of(savedOrderItem.getId()));

        Optional<OrderItem> itemFound =  orderItemRepository.findByIdAndIsActiveTrue(savedOrderItem.getId());

        assertEquals(Optional.empty(), itemFound);
    }
}
