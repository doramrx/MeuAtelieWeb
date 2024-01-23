package com.meuatelieweb.backend.domain.orderitem.dto;

import com.meuatelieweb.backend.domain.customermeasure.dto.CustomerMeasureDTO;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TailoredOrderItemDTO extends OrderItemDTO {
    private Set<CustomerMeasureDTO> customerMeasures;
}
