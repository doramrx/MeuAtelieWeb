package com.meuatelieweb.backend.domain.customeradjust.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveCustomerAdjustListDTO {
    private List<SaveCustomerAdjustDTO> adjusts;
}
