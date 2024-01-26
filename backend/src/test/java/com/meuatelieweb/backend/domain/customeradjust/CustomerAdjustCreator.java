package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.customeradjust.dto.CustomerAdjustDTO;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;

import java.util.UUID;

import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.createValidAdjust;
import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.createValidAdjustDTO;

public class CustomerAdjustCreator {

    public static CustomerAdjust createValidCustomerAdjust() {
        return CustomerAdjust.builder()
                .id(UUID.randomUUID())
                .adjust(createValidAdjust())
                .adjustmentCost(createValidAdjust().getCost())
                .isActive(true)
                .build();
    }

    public static SaveCustomerAdjustDTO createValidSaveCustomerAdjustDTO() {
        return SaveCustomerAdjustDTO.builder()
                .adjustmentId(UUID.randomUUID())
                .build();
    }

    public static CustomerAdjustDTO createValidCustomerAdjustDTO(UUID id){
        return CustomerAdjustDTO.builder()
                .id(id)
                .adjust(createValidAdjustDTO(UUID.randomUUID()))
                .adjustmentCost(createValidAdjust().getCost())
                .isActive(true)
                .build();
    }
}
