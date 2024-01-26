package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.CustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;

import java.util.UUID;

import static com.meuatelieweb.backend.domain.measure.MeasureCreator.createValidMeasure;
import static com.meuatelieweb.backend.domain.measure.MeasureCreator.createValidMeasureDTO;

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

    public static CustomerMeasureDTO createValidCustomerMeasureDTO(UUID id) {
        return CustomerMeasureDTO.builder()
                .measure(createValidMeasureDTO(UUID.randomUUID()))
                .measurementValue(60.0)
                .isActive(true)
                .build();
    }
}
