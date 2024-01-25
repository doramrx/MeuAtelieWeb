package com.meuatelieweb.backend.domain.orderitem.dto;

import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureListDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveCustomerAdjustMeasureDTO {
    @NotEmpty
    private OrderType type;
    private SaveCustomerAdjustListDTO customerAdjusts;
    private SaveCustomerMeasureListDTO customerMeasures;
}
