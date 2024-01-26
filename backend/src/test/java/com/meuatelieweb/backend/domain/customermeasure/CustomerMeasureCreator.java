package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;

import java.util.UUID;

import static com.meuatelieweb.backend.domain.measure.MeasureCreator.createValidMeasure;

public class CustomerMeasureCreator {

    public static CustomerMeasure createValidCustomerMeasure() {
        return CustomerMeasure.builder()
                .id(UUID.randomUUID())
                .measure(createValidMeasure())
                .measurementValue(60.0)
                .isActive(true)
                .build();
    }

    public static SaveCustomerMeasureDTO createValidSaveCustomerMeasureDTO() {
        return SaveCustomerMeasureDTO.builder()
                .measurementId(UUID.randomUUID())
                .measurementValue(60.0)
                .build();
    }

    public static UpdateCustomerMeasureDTO createValidUpdateCustomerMeasureDTO() {
        return UpdateCustomerMeasureDTO.builder()
                .measurementValue(70.0)
                .build();
    }
}
