package com.meuatelieweb.backend.util;

import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.customermeasure.dto.CustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;

import java.util.List;
import java.util.UUID;

import static com.meuatelieweb.backend.util.MeasureCreator.createValidMeasure;
import static com.meuatelieweb.backend.util.MeasureCreator.createValidMeasureDTO;

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

    public static SaveCustomerMeasureListDTO createValidSaveCustomerMeasureListDTO() {
        return SaveCustomerMeasureListDTO.builder()
                .measures(List.of(createValidSaveCustomerMeasureDTO()))
                .build();
    }
}
