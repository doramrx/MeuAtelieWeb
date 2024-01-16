package com.meuatelieweb.backend.domain.measure;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import com.meuatelieweb.backend.domain.measure.dto.SaveUpdateMeasureDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.*;
import static com.meuatelieweb.backend.domain.measure.MeasureCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

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
        BDDMockito.when(measureConverterMock.toMeasureDTO(any(Measure.class))).thenReturn(measureDTO);
    }

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {
        private void mockRepositoryFindAll(PageImpl<Measure> pageable) {
            BDDMockito.when(measureRepositoryMock.findAll(any(Specification.class), any(PageRequest.class)))
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

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(){
            BDDMockito.when(measureRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.of(createValidMeasure()));
        }

        @Test
        @DisplayName("findById returns measure when successful")
        void findById_ReturnsMeasure_WhenSuccessful() {
            MeasureDTO measureDTO = createValidMeasureDTO(UUID.randomUUID());

            this.mockRepositoryFindById();

            mockConverterToMeasureDTO(measureDTO);

            MeasureDTO measureFound = measureService.findById(UUID.randomUUID());

            assertNotNull(measureFound);
            assertEquals(measureDTO.getId(), measureFound.getId());
            assertEquals(measureDTO.getName(), measureFound.getName());
            assertEquals(measureDTO.getIsActive(), measureFound.getIsActive());
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when measure is not found")
        void findById_ThrowsEntityNotFoundException_WhenMeasureIsNotFound() {
            BDDMockito.when(measureRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> measureService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when id measure is null")
        void findById_ThrowsEntityNotFoundException_WhenIdMeasureIsNull() {
            assertThrows(EntityNotFoundException.class,
                    () -> measureService.findById(null));
        }
    }

    @DisplayName("Test addMeasure method")
    @Nested
    class AddMeasureTests {

        private void mockRepositorySave(){
            BDDMockito.when(measureRepositoryMock.save(any(Measure.class)))
                    .thenReturn(createValidMeasure());
        }

        @Test
        @DisplayName("addMeasure returns measure when successful")
        void addMeasure_ReturnsMeasure_WhenSuccessful() {
            MeasureDTO measureDTO = createValidMeasureDTO(createValidMeasure().getId());

            this.mockRepositorySave();

            mockConverterToMeasureDTO(measureDTO);

            MeasureDTO measureSaved = measureService.addMeasure(createValidSaveUpdateMeasureDTO());

            assertNotNull(measureSaved);
            assertEquals(measureDTO, measureSaved);
        }

        @Test
        @DisplayName("addMeasure throws IllegalArgumentException when measure is null")
        void addMeasure_ThrowsIllegalArgumentException_WhenMeasureIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> measureService.addMeasure(null));
        }

        @Test
        @DisplayName("addMeasure throws IllegalArgumentException when measure name is null")
        void addMeasure_ThrowsIllegalArgumentException_WhenMeasureNameIsNull() {
            SaveUpdateMeasureDTO saveUpdateMeasureDTO = createValidSaveUpdateMeasureDTO();
            saveUpdateMeasureDTO.setName(null);

            assertThrows(IllegalArgumentException.class,
                    () -> measureService.addMeasure(saveUpdateMeasureDTO));
        }

        @Test
        @DisplayName("addMeasure throws DuplicateKeyException when measure name already exists")
        void addMeasure_ThrowsDuplicateKeyException_WhenMeasureNameAlreadyExists() {
            BDDMockito.when(measureRepositoryMock.existsByName(anyString())).thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> measureService.addMeasure(createValidSaveUpdateMeasureDTO()));
        }
    }

}
