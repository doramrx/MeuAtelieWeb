package com.meuatelieweb.backend.domain.orderitem.dto;

import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveOrderItemDTO {

    @NotBlank(message = "Order item type cannot be null")
    private OrderType type;

    @NotBlank(message = "Order item title cannot be null")
    private String title;

    private String description;

    @Positive(message = "The given cost cannot be lesser than 0.01")
    @Digits(integer = 4, fraction = 2, message = "The cost must have up to 4 digits in total, with 2 decimals")
    private Double cost;

    private Set<SaveCustomerMeasureDTO> measures;

    private Set<SaveCustomerAdjustDTO> adjusts;

    private LocalDateTime dueDate;
}
