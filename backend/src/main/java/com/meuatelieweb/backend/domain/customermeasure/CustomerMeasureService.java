package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.measure.MeasureService;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerMeasureService {

    @Autowired
    private CustomerMeasureRepository repository;

    @Autowired
    private MeasureService measureService;

    @Transactional
    public List<CustomerMeasure> addCustomerMeasures(
            @NonNull
            OrderItem item,
            @NonNull
            Set<SaveCustomerMeasureDTO> saveCustomerMeasures
    ) {

        this.validateItemMeasureValues(saveCustomerMeasures);

        Set<Measure> measures = measureService.getMeasures(
                saveCustomerMeasures.stream()
                        .map(SaveCustomerMeasureDTO::getMeasurementId)
                        .collect(Collectors.toSet())
        );

        List<CustomerMeasure> customerMeasures = saveCustomerMeasures.stream().map(customerMeasure ->
                CustomerMeasure.builder()
                        .measurementValue(customerMeasure.getMeasurementValue())
                        .measure(measures.stream()
                                .filter(measure -> customerMeasure.getMeasurementId().equals(measure.getId()))
                                .findFirst().get()
                        )
                        .orderItem(item)
                        .build()
        ).toList();

        return repository.saveAllAndFlush(customerMeasures);
    }

    private void validateItemMeasureValues(Set<SaveCustomerMeasureDTO> saveCustomerMeasures) {
        List<Double> measureValues = saveCustomerMeasures.stream()
                .map(SaveCustomerMeasureDTO::getMeasurementValue).toList();

        for (Double value : measureValues) {
            if (value == null) {
                throw new IllegalArgumentException("The given measure value cannot be empty");
            }
            if (value < 0.01) {
                throw new IllegalArgumentException("The given measure value cannot be lesser than 0.01");
            }
        }
    }
}
