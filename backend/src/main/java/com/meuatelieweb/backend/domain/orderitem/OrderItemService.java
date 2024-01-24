package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjustService;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasureService;
import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository repository;

    @Autowired
    private CustomerAdjustService customerAdjustService;

    @Autowired
    private CustomerMeasureService customerMeasureService;

    @Transactional
    public List<OrderItem> addOrderItems(
            @NonNull
            Order order,
            @NonNull
            Set<SaveOrderItemDTO> items
    ) {

        if (items.isEmpty()) {
            throw new IllegalArgumentException("The order must have at least one item");
        }

        List<OrderItem> orderItems = items.stream().map(item -> {
            this.validateItemType(item.getType());
            this.validateItemTitle(item.getTitle());
            this.validateItemDueDate(item.getDueDate());

            if (item.getType() == OrderType.ADJUST) {
                AdjustOrderItem adjustOrderItem = new AdjustOrderItem();
                adjustOrderItem.setTitle(item.getTitle());
                adjustOrderItem.setDescription(item.getDescription());
                adjustOrderItem.setOrder(order);
                adjustOrderItem.setCreatedAt(LocalDateTime.now());
                adjustOrderItem.setDueDate(item.getDueDate());

                AdjustOrderItem adjustItem = repository.saveAndFlush(adjustOrderItem);

                List<CustomerAdjust> customerAdjusts = customerAdjustService.addCustomerAdjusts(
                        adjustItem, item.getAdjusts()
                );

                adjustItem.setCost(
                        customerAdjusts.stream().mapToDouble(CustomerAdjust::getAdjustmentCost).sum()
                );

                adjustItem.setCustomerAdjustments(customerAdjusts);

                return adjustItem;
            }

            this.validateTailoredItemCost(item.getCost());

            TailoredOrderItem tailoredOrderItem = new TailoredOrderItem();
            tailoredOrderItem.setTitle(item.getTitle());
            tailoredOrderItem.setDescription(item.getDescription());
            tailoredOrderItem.setCost(item.getCost());
            tailoredOrderItem.setOrder(order);
            tailoredOrderItem.setCreatedAt(LocalDateTime.now());
            tailoredOrderItem.setDueDate(item.getDueDate());

            TailoredOrderItem tailoredItem = repository.saveAndFlush(tailoredOrderItem);

            List<CustomerMeasure> customerMeasures = customerMeasureService.addCustomerMeasures(
                    tailoredItem, item.getMeasures()
            );

            tailoredItem.setCustomerMeasures(customerMeasures);

            return tailoredItem;

        }).toList();

        return repository.saveAllAndFlush(orderItems);
    }

    private void validateItemType(OrderType type) {
        if (type == null) {
            throw new IllegalArgumentException("Item type cannot be null");
        }
        if (!type.equals(OrderType.ADJUST) && !type.equals(OrderType.TAILORED)) {
            throw new HttpMessageConversionException("Item type must be ADJUST or TAILORED");
        }
    }

    private void validateItemTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Item title cannot be null");
        }
    }

    private void validateItemDueDate(LocalDateTime dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Item due date cannot be null");
        }
        if (dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Item due date is invalid");
        }
    }

    private void validateTailoredItemCost(Double cost) {
        if (cost == null) {
            throw new IllegalArgumentException("The given cost cannot be empty");
        }
        if (cost < 0.01) {
            throw new IllegalArgumentException("The given cost cannot be lesser than 0.01");
        }
    }

    @Transactional
    public void deleteOrderItems(@NonNull List<OrderItem> items) {

        Set<UUID> ids = items.stream()
                .map(OrderItem::getId)
                .collect(Collectors.toSet());

        if (!repository.existsByIdIn(ids)) {
            throw new EntityNotFoundException("Some of the given order items do not exist");
        }

        items.forEach(item -> {
            if (item instanceof AdjustOrderItem adjustOrderItem) {

                if (!adjustOrderItem.getCustomerAdjustments().isEmpty()) {
                    Set<UUID> customerAdjustsIds = adjustOrderItem.getCustomerAdjustments().stream()
                            .map(CustomerAdjust::getId)
                            .collect(Collectors.toSet());

                    customerAdjustService.deleteCustomerAdjusts(customerAdjustsIds);
                }
            }

            if (item instanceof TailoredOrderItem tailoredOrderItem) {

                if (!tailoredOrderItem.getCustomerMeasures().isEmpty()) {
                    Set<UUID> customerMeasuresIds = tailoredOrderItem.getCustomerMeasures().stream()
                            .map(CustomerMeasure::getId)
                            .collect(Collectors.toSet());

                    customerMeasureService.deleteCustomerMeasures(customerMeasuresIds);
                }
            }
        });

        repository.inactivateOrderItemById(ids);
    }

    @Transactional
    public void deliverItem(UUID id) {

        OrderItem orderItem = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given item does not exist or is already inactive"));

        if (orderItem.getDeliveredAt() != null){
            throw new IllegalArgumentException("The given item is already delivered");
        }

        orderItem.setDeliveredAt(LocalDateTime.now());

        repository.save(orderItem);
    }
}
