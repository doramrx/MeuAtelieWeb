package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjustService;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasureService;
import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
        if (dueDate.isBefore(LocalDateTime.now())){
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

}
