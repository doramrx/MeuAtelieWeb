package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.adjust.AdjustConverter;
import com.meuatelieweb.backend.domain.customeradjust.dto.CustomerAdjustDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerAdjustConverter {

    @Autowired
    private AdjustConverter adjustConverter;

    public CustomerAdjustDTO toCustomerAdjustDTO(@NonNull CustomerAdjust customerAdjust) {
        return new CustomerAdjustDTO()
                .builder()
                .id(customerAdjust.getId())
                .adjust(adjustConverter.toAdjustDTO(customerAdjust.getAdjust()))
                .adjustmentCost(customerAdjust.getAdjustmentCost())
                .isActive(customerAdjust.getIsActive())
                .build();
    }
}
