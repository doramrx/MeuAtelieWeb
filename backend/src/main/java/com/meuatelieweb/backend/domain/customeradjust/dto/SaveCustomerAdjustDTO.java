package com.meuatelieweb.backend.domain.customeradjust.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveCustomerAdjustDTO {

    @NotBlank(message = "The given id cannot be null")
    private UUID adjustmentId;
}
