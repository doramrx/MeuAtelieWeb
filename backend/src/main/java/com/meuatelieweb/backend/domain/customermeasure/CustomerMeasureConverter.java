package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.CustomerMeasureDTO;
import com.meuatelieweb.backend.domain.measure.MeasureConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerMeasureConverter {

    @Autowired
    private MeasureConverter measureConverter;

    public CustomerMeasureDTO toCustomerMeasureDTO(CustomerMeasure customerMeasure) {
        return new CustomerMeasureDTO()
                .builder()
                .id(customerMeasure.getId())
                .measure(measureConverter.toMeasureDTO(customerMeasure.getMeasure()))
                .measurementValue(customerMeasure.getMeasurementValue())
                .isActive(customerMeasure.getIsActive())
                .build();
    }
}
