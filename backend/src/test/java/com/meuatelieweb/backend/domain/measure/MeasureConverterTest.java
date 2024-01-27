package com.meuatelieweb.backend.domain.measure;

import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.meuatelieweb.backend.util.MeasureCreator.createValidMeasure;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Measure Converter")
class MeasureConverterTest {

    @InjectMocks
    private MeasureConverter measureConverter;

    @Test
    @DisplayName("toMeasureDTO returns MeasureDTO when successful")
    void toMeasureDTO_ReturnsMeasureDTO_WhenSuccessful() {
        Measure measure = createValidMeasure();

        MeasureDTO measureDTO = measureConverter.toMeasureDTO(measure);

        assertNotNull(measureDTO);
        assertEquals(measure.getId(), measureDTO.getId());
        assertEquals(measure.getName(), measureDTO.getName());
        assertEquals(measure.getIsActive(), measureDTO.getIsActive());
    }

    @Test
    @DisplayName("toMeasureDTO throws IllegalArgumentException when measure is null")
    void toMeasureDTO_ThrowsIllegalArgumentException_WhenMeasureIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> measureConverter.toMeasureDTO(null));
    }
}
