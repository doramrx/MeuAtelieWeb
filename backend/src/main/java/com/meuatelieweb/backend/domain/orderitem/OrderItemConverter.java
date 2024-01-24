package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjustConverter;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasureConverter;
import com.meuatelieweb.backend.domain.orderitem.dto.AdjustOrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.OrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.TailoredOrderItemDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class OrderItemConverter {

    @Autowired
    private CustomerAdjustConverter customerAdjustConverter;

    @Autowired
    private CustomerMeasureConverter customerMeasureConverter;

    public OrderItemDTO toOrderItemDTO(@NonNull OrderItem orderItem) {

        if (orderItem instanceof AdjustOrderItem) {
            AdjustOrderItemDTO adjustOrderItem = new AdjustOrderItemDTO();
            adjustOrderItem.setId(orderItem.getId());
            adjustOrderItem.setType(OrderType.ADJUST);
            adjustOrderItem.setTitle(orderItem.getTitle());
            adjustOrderItem.setDescription(orderItem.getDescription());
            adjustOrderItem.setCustomerAdjusts(
                    ((AdjustOrderItem) orderItem).getCustomerAdjustments().stream()
                            .map(customerAdjustConverter::toCustomerAdjustDTO)
                            .collect(Collectors.toSet())
            );
            adjustOrderItem.setCost(orderItem.getCost());
            adjustOrderItem.setCreatedAt(orderItem.getCreatedAt());
            adjustOrderItem.setDueDate(orderItem.getDueDate());
            adjustOrderItem.setDeliveredAt(orderItem.getDeliveredAt());
            adjustOrderItem.setIsActive(orderItem.getIsActive());

            return adjustOrderItem;
        }

        TailoredOrderItemDTO tailoredOrderItem = new TailoredOrderItemDTO();
        tailoredOrderItem.setId(orderItem.getId());
        tailoredOrderItem.setType(OrderType.TAILORED);
        tailoredOrderItem.setTitle(orderItem.getTitle());
        tailoredOrderItem.setDescription(orderItem.getDescription());
        tailoredOrderItem.setCustomerMeasures(
                ((TailoredOrderItem) orderItem).getCustomerMeasures().stream()
                        .map(customerMeasureConverter::toCustomerMeasureDTO)
                        .collect(Collectors.toSet())
        );
        tailoredOrderItem.setCost(orderItem.getCost());
        tailoredOrderItem.setCreatedAt(orderItem.getCreatedAt());
        tailoredOrderItem.setDueDate(orderItem.getDueDate());
        tailoredOrderItem.setDeliveredAt(orderItem.getDeliveredAt());
        tailoredOrderItem.setIsActive(orderItem.getIsActive());

        return tailoredOrderItem;
    }
}
