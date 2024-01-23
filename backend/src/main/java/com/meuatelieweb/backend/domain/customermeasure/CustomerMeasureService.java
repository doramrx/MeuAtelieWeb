package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.measure.MeasureService;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerMeasureService {

    @Autowired
    private CustomerMeasureRepository repository;

    @Autowired
    private MeasureService measureService;

/*    @Transactional
    public CustomerMeasure addCustomerMeasure(
            @NonNull
            SaveCustomerMeasureDTO saveCustomerMeasureDTO,
            @NonNull
            OrderItem orderItem
    ) {

        Measure measure = measureService.findByNameAndIsActiveTrue(saveCustomerMeasureDTO.getMeasurementName());

        CustomerMeasure customerMeasure = CustomerMeasure.builder()
                .measure(measure)
                .measurementValue(saveCustomerMeasureDTO.getMeasurementValue())
                .orderItem(orderItem)
                .build();

        return repository.save(customerMeasure);
    }*/
}
