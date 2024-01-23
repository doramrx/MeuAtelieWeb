package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjustService;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasureService;
import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository repository;

//    @Autowired
//    private OrderService orderService;

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
            if (item.getType() == OrderType.ADJUST) {
                AdjustOrderItem adjustOrderItem = new AdjustOrderItem();
                adjustOrderItem.setTitle(item.getTitle());
                adjustOrderItem.setDescription(item.getDescription());
                adjustOrderItem.setOrder(order);

                List<CustomerAdjust> customerAdjusts = customerAdjustService.addCustomerAdjusts(
                        adjustOrderItem, item.getAdjusts()
                );

                adjustOrderItem.setCustomerAdjustments(customerAdjusts);

                return adjustOrderItem;
            }

            TailoredOrderItem tailoredOrderItem = new TailoredOrderItem();
            tailoredOrderItem.setTitle(item.getTitle());
            tailoredOrderItem.setDescription(item.getDescription());
            tailoredOrderItem.setOrder(order);

            return tailoredOrderItem;
        }).toList();

//
//        Order order = orderService.findById(idOrder);
//
//        OrderItem orderItem = this.createOrderItemFromDTO(saveOrderItemDTO, order);
//        OrderItem savedOrderItem = repository.save(orderItem);
//
//        if (saveOrderItemDTO.getType().equals(OrderType.ADJUST)) {
//            return updateOrderItemWithAdjusts(saveOrderItemDTO, (AdjustOrderItem) savedOrderItem);
//        } else {
//            return updateOrderItemWithMeasures(saveOrderItemDTO, (TailoredOrderItem) savedOrderItem);
//        }
        return repository.saveAllAndFlush(orderItems);
    }
/*
    private OrderItem createOrderItemFromDTO(SaveOrderItemDTO saveOrderItemDTO, Order order) {

        this.validateOrderItemType(saveOrderItemDTO.getType());

        if (saveOrderItemDTO.getType().equals(OrderType.ADJUST)) {
            return this.createAdjustOrderItemFromDTO(saveOrderItemDTO, order);
        } else {
            return this.createTailoredOrderItemFromDTO(saveOrderItemDTO, order);
        }
    }

    private void validateOrderItemType(OrderType type) {
        if (type == null) {
            throw new IllegalArgumentException("Order item type cannot be null");
        }

        if (!type.equals(OrderType.ADJUST) && !type.equals(OrderType.TAILORED)) {
            throw new IllegalArgumentException("Order item type must be ADJUST or TAILORED type");
        }
    }

    private OrderItem createAdjustOrderItemFromDTO(SaveOrderItemDTO saveOrderItemDTO, Order order) {
        return AdjustOrderItem.builder()
                .title(saveOrderItemDTO.getTitle())
                .description(saveOrderItemDTO.getDescription())
                .order(order)
                .build();
    }

    private OrderItem createTailoredOrderItemFromDTO(SaveOrderItemDTO saveOrderItemDTO, Order order) {
        return TailoredOrderItem.builder()
                .title(saveOrderItemDTO.getTitle())
                .description(saveOrderItemDTO.getDescription())
                .cost(saveOrderItemDTO.getCost())
                .order(order)
                .build();
    }

    @Transactional
    public OrderItem updateOrderItemWithAdjusts(SaveOrderItemDTO saveOrderItemDTO, AdjustOrderItem orderItem) {
        Set<CustomerAdjust> adjusts = new HashSet<>();
        Double totalAdjustmentsCost = 0.0;

        for (SaveCustomerAdjustDTO saveAdjustDTO : saveOrderItemDTO.getAdjusts()) {
            CustomerAdjust savedCustomerAdjust = customerAdjustService.addCustomerAdjust(saveAdjustDTO, orderItem);
            totalAdjustmentsCost += saveAdjustDTO.getAdjustmentCost();
            adjusts.add(savedCustomerAdjust);
        }

        orderItem.setCost(totalAdjustmentsCost);
        orderItem.setCustomerAdjustments(adjusts);

        return repository.save(orderItem);
    }

    @Transactional
    public OrderItem updateOrderItemWithMeasures(SaveOrderItemDTO saveOrderItemDTO, TailoredOrderItem orderItem) {
        Set<CustomerMeasure> measures = new HashSet<>();

        for (SaveCustomerMeasureDTO saveMeasureDTO : saveOrderItemDTO.getMeasures()) {
            CustomerMeasure savedCustomerMeasure = customerMeasureService.addCustomerMeasure(saveMeasureDTO, orderItem);
            measures.add(savedCustomerMeasure);
        }

        orderItem.setCustomerMeasures(measures);

        return repository.save(orderItem);
    }*/
}
