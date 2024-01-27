package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.CustomerMeasureDTO;
import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.measure.MeasureConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.meuatelieweb.backend.util.CustomerMeasureCreator.createValidCustomerMeasure;
import static com.meuatelieweb.backend.util.MeasureCreator.createValidMeasure;
import static com.meuatelieweb.backend.util.MeasureCreator.createValidMeasureDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Measure Converter")
class CustomerMeasureConverterTest {

    @InjectMocks
    private CustomerMeasureConverter customerMeasureConverter;

    @Mock
    private MeasureConverter measureConverterMock;

    private void mockMeasureConverterToMeasureDTO(UUID measureId) {
        BDDMockito.when(measureConverterMock.toMeasureDTO(any(Measure.class)))
                .thenReturn(createValidMeasureDTO(measureId));
    }

    @Test
    @DisplayName("toCustomerMeasureDTO returns CustomerMeasureDTO when successful")
    void toCustomerMeasureDTO_ReturnsCustomerMeasureDTO_WhenSuccessful() {
        CustomerMeasure customerMeasure = createValidCustomerMeasure();
        Measure measure = createValidMeasure();
        customerMeasure.setMeasure(measure);
        this.mockMeasureConverterToMeasureDTO(measure.getId());

        CustomerMeasureDTO customerMeasureDTO = customerMeasureConverter.toCustomerMeasureDTO(customerMeasure);

        assertNotNull(customerMeasureDTO);
        assertEquals(customerMeasure.getId(), customerMeasureDTO.getId());
        assertEquals(customerMeasure.getMeasure().getId(), customerMeasureDTO.getMeasure().getId());
        assertEquals(customerMeasure.getMeasure().getName(), customerMeasureDTO.getMeasure().getName());
        assertEquals(customerMeasure.getMeasurementValue(), customerMeasureDTO.getMeasurementValue());
        assertEquals(customerMeasure.getIsActive(), customerMeasureDTO.getIsActive());
    }

    @Test
    @DisplayName("toCustomerMeasureDTO throws IllegalArgumentException when customer measure is null")
    void toCustomerMeasureDTO_ThrowsIllegalArgumentException_WhenCustomerMeasureIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> customerMeasureConverter.toCustomerMeasureDTO(null));
    }
}
