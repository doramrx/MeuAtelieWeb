package com.meuatelieweb.backend.domain.customermeasure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveCustomerMeasureListDTO {
    private List<SaveCustomerMeasureDTO> measures;
}
