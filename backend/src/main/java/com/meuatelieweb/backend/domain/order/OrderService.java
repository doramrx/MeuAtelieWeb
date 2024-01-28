package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.UpdateOrderDTO;
import com.meuatelieweb.backend.domain.orderitem.AdjustOrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderItemService;
import com.meuatelieweb.backend.domain.orderitem.TailoredOrderItem;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.UpdateOrderItemDTO;
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
            LocalDateTime createdAt,
            LocalDateTime finishedAt,
            String customerName,
            String customerEmail,
            Boolean isActive) {
        Specification<Order> specification = OrderSpecification.applyFilter(
                orderNumber, createdAt, finishedAt, customerName, customerEmail, isActive
        );

        return repository.findAll(specification, pageable);
    }

    public Order findById(@NonNull UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The given order does not exist"));
    }

    public Order findByIdAndIsActiveTrue(@NonNull UUID id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given order does not exist or is already inactive"));
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

        return savedOrder;
    }

    @Transactional
    public Order addOrderItemToOrder(
            @NonNull UUID orderId,
            @NonNull SaveOrderItemDTO saveOrderItem
    ) {
        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        orderItemService.addOrderItems(order, Set.of(saveOrderItem));

        order.setUpdatedAt(LocalDateTime.now());

        return repository.saveAndFlush(order);
    }

    @Transactional
    public Order addAdjustsToOrderItem(
            @NonNull UUID orderId,
            @NonNull UUID itemId,
            @NonNull SaveCustomerAdjustListDTO saveCustomerAdjustList
    ) {
        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        if (saveCustomerAdjustList.getAdjusts().isEmpty()) {
            throw new IllegalArgumentException("There are no adjusts to add to order item");
        }

        AdjustOrderItem adjustOrderItem = orderItemService.addAdjustsToOrderItem(itemId, saveCustomerAdjustList);
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(
                order.getOrderItems().stream().map(
                        item -> item.getId().equals(adjustOrderItem.getId()) ? adjustOrderItem : item
                ).toList()
        );

        return order;
    }

    @Transactional
    public Order addMeasuresToOrderItem(
            @NonNull UUID orderId,
            @NonNull UUID itemId,
            @NonNull SaveCustomerMeasureListDTO saveCustomerMeasureList
    ) {
        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        if (saveCustomerMeasureList.getMeasures().isEmpty()) {
            throw new IllegalArgumentException("There are no measures to add to order item");
        }

        TailoredOrderItem tailoredOrderItem = orderItemService.addMeasuresToOrderItem(itemId, saveCustomerMeasureList);
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(
                order.getOrderItems().stream().map(
                        item -> item.getId().equals(tailoredOrderItem.getId()) ? tailoredOrderItem : item
                ).toList()
        );

        return order;
    }

    @Transactional
    public Order updateOrder(@NonNull UUID id, @NonNull UpdateOrderDTO updateOrderDTO) {

        this.validateIfOrderWasFinished(id);
        Order order = this.findByIdAndIsActiveTrue(id);

        Customer customer = customerService.findByIdAndIsActiveTrue(updateOrderDTO.getCustomerId());
        order.setCustomer(customer);

        order.setUpdatedAt(LocalDateTime.now());
        return repository.save(order);
    }

    @Transactional
    public Order updateOrderItem(
            @NonNull UUID orderId,
            @NonNull UUID itemId,
            @NonNull UpdateOrderItemDTO updateOrderItemDTO) {

        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        orderItemService.updateOrderItem(itemId, updateOrderItemDTO);

        order.setUpdatedAt(LocalDateTime.now());
        return repository.save(order);
    }

    @Transactional
    public Order updateOrderItemCustomerMeasure(
            @NonNull UUID orderId,
            @NonNull UUID itemId,
            @NonNull UUID customerMeasureId,
            @NonNull UpdateCustomerMeasureDTO updateCustomerMeasureDTO
    ) {
        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        orderItemService.updateOrderItemCustomerMeasure(itemId, customerMeasureId, updateCustomerMeasureDTO);

        order.setUpdatedAt(LocalDateTime.now());
        return repository.saveAndFlush(order);
    }

    @Transactional
    public void deliverItem(@NonNull UUID orderId, @NonNull UUID itemId) {

        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

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
    public void deleteOrder(@NonNull UUID id) {

        Order order = this.findByIdAndIsActiveTrue(id);

        orderItemService.deleteOrderItems(order.getOrderItems());

        repository.inactivateOrderById(id);
    }

    @Transactional
    public Order singleDeleteCustomerAdjustFromItem(
            @NonNull UUID orderId,
            @NonNull UUID itemId,
            @NonNull UUID customerAdjustId
    ) {

        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        orderItemService.singleDeleteCustomerAdjustFromItem(itemId, customerAdjustId);

        order.setUpdatedAt(LocalDateTime.now());

        return repository.save(order);
    }

    @Transactional
    public Order singleDeleteCustomerMeasureFromItem(
            @NonNull UUID orderId,
            @NonNull UUID itemId,
            @NonNull UUID customerMeasureId
    ) {

        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        orderItemService.singleDeleteCustomerMeasureFromItem(itemId, customerMeasureId);

        order.setUpdatedAt(LocalDateTime.now());

        return repository.save(order);
    }

    @Transactional
    public void singleDeleteItemFromOrder(@NonNull UUID orderId, @NonNull UUID itemId) {

        this.validateIfOrderWasFinished(orderId);
        Order order = this.findByIdAndIsActiveTrue(orderId);

        OrderItem item = orderItemService.findActiveOrderItemById(itemId);

        orderItemService.singleDeleteItemFromOrder(item);

        if (!this.validateIfOrderHasActiveItems(order.getOrderItems())) {
            this.deleteOrder(orderId);
        } else {
            order.setUpdatedAt(LocalDateTime.now());
            repository.save(order);
        }
    }

    private Boolean validateIfOrderHasActiveItems(List<OrderItem> items) {
        long notActiveItems = items.stream()
                .filter(item -> !item.getIsActive())
                .count();

        return notActiveItems != items.size();
    }

    private void validateIfOrderWasFinished(@NonNull UUID id) {
        if (!repository.existsByIdAndFinishedAtNull(id)) {
            throw new IllegalArgumentException("The order cannot be modified because it has already been finished");
        }
    }
}


