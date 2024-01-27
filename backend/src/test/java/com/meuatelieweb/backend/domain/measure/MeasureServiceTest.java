package com.meuatelieweb.backend.domain.measure;

import com.meuatelieweb.backend.domain.measure.dto.SaveUpdateMeasureDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.meuatelieweb.backend.util.MeasureCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.atMostOnce;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Measure Service")
class MeasureServiceTest {

    @InjectMocks
    private MeasureService measureService;

    @Mock
    private MeasureRepository measureRepositoryMock;

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

            List<Measure> measureList = measureService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(measureList);
            assertFalse(measureList.isEmpty());
            assertEquals(1, measureList.size());
            assertEquals(validMeasures.get(0), measureList.get(0));
        }

        @Test
        @DisplayName("findAll returns an empty page of measures when measures do not exist")
        void findAll_ReturnsEmptyPageOfMeasures_WhenMeasuresDoNotExist() {
            PageImpl<Measure> pageable = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<Measure> measureList = measureService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(measureList);
            assertTrue(measureList.isEmpty());
        }
    }

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(Measure measure){
            BDDMockito.when(measureRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(measure));
        }

        @Test
        @DisplayName("findById returns measure when successful")
        void findById_ReturnsMeasure_WhenSuccessful() {
            Measure measure = createValidMeasure();

            this.mockRepositoryFindById(measure);

            Measure measureFound = measureService.findById(UUID.randomUUID());

            assertNotNull(measureFound);
            assertEquals(measure.getId(), measureFound.getId());
            assertEquals(measure.getName(), measureFound.getName());
            assertEquals(measure.getIsActive(), measureFound.getIsActive());
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

        @Test
        @DisplayName("addMeasure returns measure when successful")
        void addMeasure_ReturnsMeasure_WhenSuccessful() {
            SaveUpdateMeasureDTO saveUpdateMeasureDTO = createValidSaveUpdateMeasureDTO();
            ArgumentCaptor<Measure> argumentCaptor = ArgumentCaptor.forClass(Measure.class);

            measureService.addMeasure(createValidSaveUpdateMeasureDTO());
            verify(measureRepositoryMock, atMostOnce()).save(argumentCaptor.capture());

            Measure savedMeasure = argumentCaptor.getValue();

            assertNotNull(savedMeasure);
            assertEquals(saveUpdateMeasureDTO.getName(), savedMeasure.getName());
            assertTrue(savedMeasure.getIsActive());
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

    @DisplayName("Test updateMeasure method")
    @Nested
    class UpdateMeasureTests {

        private void mockRepositoryFindByIdAndIsActiveTrue(Measure measure) {
            BDDMockito.when(measureRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(measure));
        }

        private void mockRepositorySave(){
            BDDMockito.when(measureRepositoryMock.save(any(Measure.class)))
                    .thenReturn(createValidMeasure());
        }

        @Test
        @DisplayName("updateMeasure updates measure when successful")
        void updateMeasure_UpdatesMeasure_WhenSuccessful() {
            SaveUpdateMeasureDTO validSaveUpdateMeasureDTO = createValidSaveUpdateMeasureDTO();
            Measure measureSpy = spy(createValidMeasure());

            this.mockRepositoryFindByIdAndIsActiveTrue(measureSpy);
            this.mockRepositorySave();

            measureService.updateMeasure(
                    UUID.randomUUID(), createValidSaveUpdateMeasureDTO()
            );

            verify(measureSpy, atLeastOnce()).setName(validSaveUpdateMeasureDTO.getName());
            verify(measureRepositoryMock, atMostOnce()).save(any(Measure.class));
        }

        @Test
        @DisplayName("updateMeasure throws IllegalArgumentException when measure is null")
        void updateMeasure_ThrowsIllegalArgumentException_WhenMeasureIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> measureService.updateMeasure(UUID.randomUUID(), null));
        }

        @Test
        @DisplayName("updateMeasure throws IllegalArgumentException when measure id is null")
        void updateMeasure_ThrowsIllegalArgumentException_WhenMeasureIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> measureService.updateMeasure(null, createValidSaveUpdateMeasureDTO()));
        }

        @Test
        @DisplayName("updateMeasure throws EntityNotFoundException when measure does not exist")
        void updateMeasure_ThrowsEntityNotFoundException_WhenMeasureDoesNotExist() {

            this.mockRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> measureService.updateMeasure(UUID.randomUUID(), createValidSaveUpdateMeasureDTO()));
        }

        @Test
        @DisplayName("updateMeasure throws DuplicateKeyException when measure name already exists")
        void updateMeasure_ThrowsDuplicateKeyException_WhenMeasureNameAlreadyExists() {

            this.mockRepositoryFindByIdAndIsActiveTrue(createValidMeasure());

            BDDMockito.when(measureRepositoryMock.existsByNameAndIdNot(anyString(), any(UUID.class)))
                    .thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> measureService.updateMeasure(UUID.randomUUID(), createValidSaveUpdateMeasureDTO()));
        }
    }

    @DisplayName("Test deleteMeasure method")
    @Nested
    class DeleteMeasureTest {

        private void mockRepositoryExistsByIdAndIsActiveTrue(Boolean existsOrIsActive) {
            BDDMockito.when(measureRepositoryMock.existsByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(existsOrIsActive);
        }

        @Test
        @DisplayName("deleteMeasure inactivates measure when successful")
        void deleteMeasure_InactivatesMeasure_WhenSuccessful() {

            this.mockRepositoryExistsByIdAndIsActiveTrue(true);

            assertDoesNotThrow(() -> measureService.deleteMeasure(UUID.randomUUID()));
        }

        @Test
        @DisplayName("deleteMeasure throws EntityNotFoundException when measure does not exist or is already inactive")
        void deleteMeasure_ThrowsEntityNotFoundException_WhenMeasureDoesNotExistOrIsAlreadyInactive() {

            this.mockRepositoryExistsByIdAndIsActiveTrue(false);

            assertThrows(EntityNotFoundException.class,
                    () -> measureService.deleteMeasure(UUID.randomUUID()));
        }

        @Test
        @DisplayName("deleteMeasure throws EntityNotFoundException when measure id is null")
        void deleteMeasure_ThrowsEntityNotFoundException_WhenMeasureIdIsNull() {

            assertThrows(EntityNotFoundException.class,
                    () -> measureService.deleteMeasure(null));
        }
    }
}
