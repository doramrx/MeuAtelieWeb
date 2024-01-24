package com.meuatelieweb.backend.domain.customeradjust.dto;

import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerAdjustDTO {
    private UUID id;
    private AdjustDTO adjust;
    private Double adjustmentCost;
    private Boolean isActive;
}
