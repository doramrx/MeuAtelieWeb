package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.measure.MeasureService;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerMeasureService {

    @Autowired
    private CustomerMeasureRepository repository;

    @Autowired
    private MeasureService measureService;

    public CustomerMeasure findById(@NonNull UUID id) {
        return repository.findCustomerMeasureById(id)
                .orElseThrow(() -> new EntityNotFoundException("The given customer measure does not exist"));
    }

    @Transactional
    public List<CustomerMeasure> addCustomerMeasures(
            @NonNull
            OrderItem item,
            @NonNull
            List<SaveCustomerMeasureDTO> saveCustomerMeasures
    ) {

        saveCustomerMeasures.forEach(measureValue -> {
            this.validateItemMeasureValue(measureValue.getMeasurementValue());
        });

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

    @Transactional
    public void updateCustomerMeasure(
            @NonNull UUID id,
            @NonNull UpdateCustomerMeasureDTO updateCustomerMeasure
    ) {

        CustomerMeasure customerMeasure = this.findById(id);

        if (updateCustomerMeasure.getMeasurementValue() != null) {
            this.validateItemMeasureValue(updateCustomerMeasure.getMeasurementValue());
            customerMeasure.setMeasurementValue(updateCustomerMeasure.getMeasurementValue());
        }
        repository.saveAndFlush(customerMeasure);
    }

    @Transactional
    public void deleteCustomerMeasures(@NonNull Set<UUID> ids) {
        if (!repository.existsByIdIn(ids)) {
            throw new EntityNotFoundException("Some of the given customer measures do not exist");
        }
        repository.deleteAllByIdIn(ids);
    }

    @Transactional
    public void singleDeleteCustomerMeasure(@NonNull UUID id) {
        CustomerMeasure customerMeasure = this.findById(id);
        repository.delete(customerMeasure);
    }

    private void validateItemMeasureValue(Double value){
        if (value == null) {
            throw new IllegalArgumentException("The given measure value cannot be empty");
        }
        if (value < 0.01) {
            throw new IllegalArgumentException("The given measure value cannot be lesser than 0.01");
        }
    }

}
