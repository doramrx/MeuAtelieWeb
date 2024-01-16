package com.meuatelieweb.backend.domain.measure;

import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.meuatelieweb.backend.domain.measure.MeasureCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Measure Service")
class MeasureServiceTest {

    @InjectMocks
    private MeasureService measureService;

    @Mock
    private MeasureRepository measureRepositoryMock;

    @Mock
    private MeasureConverter measureConverterMock;

    private void mockConverterToMeasureDTO(MeasureDTO measureDTO){
        when(measureConverterMock.toMeasureDTO(any(Measure.class))).thenReturn(measureDTO);
    }

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {
        private void mockRepositoryFindAll(PageImpl<Measure> pageable) {
            when(measureRepositoryMock.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(pageable);
        }

        @Test
        @DisplayName("findAll returns page of measures when successful")
        void findAll_ReturnsPageOfMeasures_WhenSuccessful() {
            List<Measure> validMeasures = List.of(createValidMeasure());
            PageImpl<Measure> pageable = new PageImpl<>(validMeasures, Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            MeasureDTO measureDTO = createValidMeasureDTO(validMeasures.get(0).getId());

            mockConverterToMeasureDTO(measureDTO);

            List<MeasureDTO> measureList = measureService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(measureList);
            assertFalse(measureList.isEmpty());
            assertEquals(1, measureList.size());
            assertEquals(measureDTO, measureList.get(0));
        }

        @Test
        @DisplayName("findAll returns an empty page of measures when measures do not exist")
        void findAll_ReturnsEmptyPageOfMeasures_WhenMeasuresDoNotExist() {
            PageImpl<Measure> pageable = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<MeasureDTO> measureList = measureService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(measureList);
            assertTrue(measureList.isEmpty());
        }
    }
}
