package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;

import java.util.UUID;

import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.createValidAdjust;
import static com.meuatelieweb.backend.domain.orderitem.OrderItemCreator.createValidAdjustOrderItem;

public class CustomerAdjustCreator {

    public static CustomerAdjust createValidCustomerAdjust() {
        return CustomerAdjust.builder()
                .id(UUID.randomUUID())
                .adjust(createValidAdjust())
                .adjustmentCost(createValidAdjust().getCost())
                .orderItem(createValidAdjustOrderItem())
                .isActive(true)
                .build();
    }

    public static SaveCustomerAdjustDTO createValidSaveCustomerAdjustDTO() {
        return SaveCustomerAdjustDTO.builder()
                .adjustmentId(UUID.randomUUID())
                .build();
    }
}
