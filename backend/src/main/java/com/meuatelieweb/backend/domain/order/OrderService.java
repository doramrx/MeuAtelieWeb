package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import com.meuatelieweb.backend.domain.orderitem.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderItemService orderItemService;

    public Page<Order> findAll(
            Pageable pageable,
            Integer orderNumber,
            LocalDateTime createdAt,
            LocalDateTime finishedAt,
            String customerName,
            String customerEmail,
            Boolean isActive)
    {
        Specification<Order> specification = OrderSpecification.applyFilter(
                orderNumber, createdAt, finishedAt, customerName, customerEmail, isActive
        );

        return repository.findAll(specification, pageable);
    }

    public Order findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("The given order does not exist"));
    }

    @Transactional
    public Order addOrder(@NonNull SaveOrderDTO saveOrderDTO) {
        Customer customer = customerService.findByIdAndIsActiveTrue(saveOrderDTO.getCustomerId());
        Integer nextOrderNumber = repository.getNextOrderNumber();

        Order order = Order.builder()
                .orderNumber(nextOrderNumber)
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .build();

        Order savedOrder = repository.saveAndFlush(order);

        List<OrderItem> orderItems = orderItemService.addOrderItems(savedOrder, saveOrderDTO.getItems());
        savedOrder.setOrderItems(orderItems);

        return repository.saveAndFlush(savedOrder);
    }

    @Transactional
    public void deleteOrder(@NonNull UUID id) {

        Order order = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given order does not exist or is already inactive"));

        orderItemService.deleteOrderItems(order.getOrderItems());

        repository.inactivateOrderById(id);
    }

    @Transactional
    public void deliverItem(UUID orderId, UUID itemId) {

        Order order = repository.findByIdAndIsActiveTrue(orderId)
                .orElseThrow(() -> new EntityNotFoundException("The given order does not exist or is already inactive"));

        orderItemService.deliverItem(itemId);

        long notDeliveredItems = order.getOrderItems().stream()
                .filter(item -> item.getDeliveredAt() == null).count();

        if (notDeliveredItems == 0) {
            order.setFinishedAt(LocalDateTime.now());
        }

        order.setUpdatedAt(LocalDateTime.now());

        repository.save(order);
    }

    @Transactional
    public Order singleDeleteCustomerAdjustFromItem(UUID orderId, UUID itemId, UUID customerAdjustId) {

        Order order = repository.findByIdAndIsActiveTrue(orderId)
                .orElseThrow(() -> new EntityNotFoundException("The given order does not exist or is already inactive"));

        orderItemService.singleDeleteCustomerAdjustFromItem(itemId, customerAdjustId);

        order.setUpdatedAt(LocalDateTime.now());

        return repository.save(order);
    }

    @Transactional
    public Order singleDeleteCustomerMeasureFromItem(UUID orderId, UUID itemId, UUID customerMeasureId) {

        Order order = repository.findByIdAndIsActiveTrue(orderId)
                .orElseThrow(() -> new EntityNotFoundException("The given order does not exist or is already inactive"));

        orderItemService.singleDeleteCustomerMeasureFromItem(itemId, customerMeasureId);

        order.setUpdatedAt(LocalDateTime.now());

        return repository.save(order);
    }
}
