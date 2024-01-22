package com.meuatelieweb.backend.domain.orderItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> findByOrderId(UUID orderId) {
//        return this.orderItemRepository.findByOrderId(orderId);
        return List.of();
    }
}
