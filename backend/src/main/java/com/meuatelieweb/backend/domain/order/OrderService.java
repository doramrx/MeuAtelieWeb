package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import com.meuatelieweb.backend.domain.orderitem.*;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            LocalDateTime dueDate,
            LocalDateTime createdAt,
            LocalDateTime deliveredAt,
            String customerName,
            String customerEmail,
            Boolean isActive)
    {
        Specification<Order> specification = OrderSpecification.applyFilter(
                orderNumber, dueDate, createdAt, deliveredAt, customerName, customerEmail, isActive
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
                .dueDate(saveOrderDTO.getDueDate())
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .build();

        List<OrderItem> orderItems = orderItemService.addOrderItems(order, saveOrderDTO.getItems());
        order.setOrderItems(orderItems);

        //
//        return this.updateOrderWithItems(saveOrderDTO, savedOrder);
        return repository.saveAndFlush(order);
    }
/*    @Transactional
    public Order updateOrderWithItems(SaveOrderDTO saveOrderDTO, Order order) {
        //Set<OrderItem> orderItems = new HashSet<>();
        Double totalCost = 0.0;

        for (SaveOrderItemDTO saveOrderItemDTO : saveOrderDTO.getItems()) {
            OrderItem savedOrderItem = orderItemService.addOrderItem(saveOrderItemDTO, order.getId());
            totalCost += savedOrderItem.getCost();
            //orderItems.add(savedOrderItem);
        }

        //order.setCost(totalCost);
        //order.setOrderItems(orderItems);

        return repository.save(order);
    }*/

}
