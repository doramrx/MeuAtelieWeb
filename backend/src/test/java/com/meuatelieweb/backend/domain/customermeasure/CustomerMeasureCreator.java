package com.meuatelieweb.backend.domain.customermeasure;

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
}
