package com.meuatelieweb.backend.domain.orderitem.dto;

import com.meuatelieweb.backend.domain.customeradjust.dto.CustomerAdjustDTO;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdjustOrderItemDTO extends OrderItemDTO {
    private Set<CustomerAdjustDTO> customerAdjusts;
}
