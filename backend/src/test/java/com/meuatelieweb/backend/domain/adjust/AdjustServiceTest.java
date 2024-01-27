package com.meuatelieweb.backend.domain.adjust;

import com.meuatelieweb.backend.domain.adjust.dto.SaveUpdateAdjustDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static com.meuatelieweb.backend.util.AdjustCreator.createValidAdjust;
import static com.meuatelieweb.backend.util.AdjustCreator.createValidSaveUpdateAdjustDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Adjust Service")
class AdjustServiceTest {

    @InjectMocks
    private AdjustService adjustService;

    @Mock
    private AdjustRepository adjustRepositoryMock;

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {
        private void mockRepositoryFindAll(PageImpl<Adjust> pageable) {
            BDDMockito.when(adjustRepositoryMock.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(pageable);
        }

        @Test
        @DisplayName("findAll returns page of adjusts when successful")
        void findAll_ReturnsPageOfAdjusts_WhenSuccessful() {
            List<Adjust> validAdjusts = List.of(createValidAdjust());
            PageImpl<Adjust> pageable = new PageImpl<>(validAdjusts, Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<Adjust> adjustList = adjustService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(adjustList);
            assertFalse(adjustList.isEmpty());
            assertEquals(1, adjustList.size());
            assertEquals(validAdjusts.get(0), adjustList.get(0));
        }

        @Test
        @DisplayName("findAll returns an empty page of adjusts when adjusts do not exist")
        void findAll_ReturnsEmptyPageOfAdjusts_WhenAdjustsDoNotExist() {
            PageImpl<Adjust> pageable = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<Adjust> adjustList = adjustService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(adjustList);
            assertTrue(adjustList.isEmpty());
        }
    }

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(Adjust adjust) {
            BDDMockito.when(adjustRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(adjust));
        }

        @Test
        @DisplayName("findById returns adjust when successful")
        void findById_ReturnsAdjust_WhenSuccessful() {
            Adjust adjust = createValidAdjust();

            this.mockRepositoryFindById(adjust);

            Adjust adjustFound = adjustService.findById(UUID.randomUUID());

            assertNotNull(adjustFound);
            assertEquals(adjust.getId(), adjustFound.getId());
            assertEquals(adjust.getName(), adjustFound.getName());
            assertEquals(adjust.getCost(), adjustFound.getCost());
            assertEquals(adjust.getIsActive(), adjustFound.getIsActive());
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when adjust is not found")
        void findById_ThrowsEntityNotFoundException_WhenAdjustIsNotFound() {
            this.mockRepositoryFindById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> adjustService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when id adjust is null")
        void findById_ThrowsEntityNotFoundException_WhenIdAdjustIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.findById(null));
        }
    }

    @DisplayName("Test addAdjust method")
    @Nested
    class AddAdjustTests {

        @Test
        @DisplayName("addAdjust returns adjust when successful")
        void addAdjust_ReturnsAdjust_WhenSuccessful() {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            ArgumentCaptor<Adjust> argumentCaptor = ArgumentCaptor.forClass(Adjust.class);

            adjustService.addAdjust(createValidSaveUpdateAdjustDTO());
            verify(adjustRepositoryMock, atMostOnce()).save(argumentCaptor.capture());

            Adjust savedAdjust = argumentCaptor.getValue();

            assertNotNull(savedAdjust);
            assertEquals(saveUpdateAdjustDTO.getName(), savedAdjust.getName());
            assertEquals(saveUpdateAdjustDTO.getCost(), savedAdjust.getCost());
            assertTrue(savedAdjust.getIsActive());
        }

        @Test
        @DisplayName("addAdjust throws IllegalArgumentException when adjust is null")
        void addAdjust_ThrowsIllegalArgumentException_WhenAdjustIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.addAdjust(null));
        }

        @Test
        @DisplayName("addAdjust throws IllegalArgumentException when adjust name is null")
        void addAdjust_ThrowsIllegalArgumentException_WhenAdjustNameIsNull() {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setName(null);

            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.addAdjust(saveUpdateAdjustDTO));
        }

        @Test
        @DisplayName("addAdjust throws DuplicateKeyException when adjust name already exists")
        void addAdjust_ThrowsDuplicateKeyException_WhenAdjustNameAlreadyExists() {
            BDDMockito.when(adjustRepositoryMock.existsByName(anyString())).thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> adjustService.addAdjust(createValidSaveUpdateAdjustDTO()));
        }

        @Test
        @DisplayName("addAdjust throws IllegalArgumentException when adjust cost is null")
        void addAdjust_ThrowsIllegalArgumentException_WhenAdjustCostIsNull() {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setCost(null);

            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.addAdjust(saveUpdateAdjustDTO));
        }

        @Test
        @DisplayName("addAdjust throws IllegalArgumentException when adjust cost is lesser than 0.01 cent")
        void addAdjust_ThrowsIllegalArgumentException_WhenAdjustCostIsLesserThan1Cent() {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setCost(0.0);

            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.addAdjust(saveUpdateAdjustDTO));
        }
    }

    @DisplayName("Test updateAdjust method")
    @Nested
    class UpdateAdjustTests {

        private void mockRepositoryFindByIdAndIsActiveTrue(Adjust adjust) {

            BDDMockito.when(adjustRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(adjust));
        }

        @Test
        @DisplayName("updateAdjust updates adjust when successful")
        void updateAdjust_UpdatesAdjust_WhenSuccessful() {
            SaveUpdateAdjustDTO validSaveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            Adjust adjustSpy = spy(createValidAdjust());

            this.mockRepositoryFindByIdAndIsActiveTrue(adjustSpy);

            adjustService.updateAdjust(
                    UUID.randomUUID(), validSaveUpdateAdjustDTO
            );

            verify(adjustSpy, atLeastOnce()).setName(validSaveUpdateAdjustDTO.getName());
            verify(adjustSpy, atLeastOnce()).setCost(validSaveUpdateAdjustDTO.getCost());
            verify(adjustRepositoryMock, atMostOnce()).save(any(Adjust.class));
        }

        @Test
        @DisplayName("updateAdjust throws IllegalArgumentException when adjust is null")
        void updateAdjust_ThrowsIllegalArgumentException_WhenAdjustIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.updateAdjust(UUID.randomUUID(), null));
        }

        @Test
        @DisplayName("updateAdjust throws IllegalArgumentException when adjust id is null")
        void updateAdjust_ThrowsIllegalArgumentException_WhenAdjustIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.updateAdjust(null, createValidSaveUpdateAdjustDTO()));
        }

        @Test
        @DisplayName("updateAdjust throws EntityNotFoundException when adjust does not exist")
        void updateAdjust_ThrowsEntityNotFoundException_WhenAdjustDoesNotExist() {

            this.mockRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> adjustService.updateAdjust(UUID.randomUUID(), createValidSaveUpdateAdjustDTO()));
        }

        @Test
        @DisplayName("updateAdjust throws DuplicateKeyException when adjust name already exists")
        void updateAdjust_ThrowsDuplicateKeyException_WhenAdjustNameAlreadyExists() {

            this.mockRepositoryFindByIdAndIsActiveTrue(createValidAdjust());

            BDDMockito.when(adjustRepositoryMock.existsByNameAndIdNot(anyString(), any(UUID.class)))
                    .thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> adjustService.updateAdjust(UUID.randomUUID(), createValidSaveUpdateAdjustDTO()));
        }

        @Test
        @DisplayName("updateAdjust throws IllegalArgumentException when adjust cost is null")
        void updateAdjust_ThrowsIllegalArgumentException_WhenAdjustCostIsNull() {

            this.mockRepositoryFindByIdAndIsActiveTrue(createValidAdjust());

            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setCost(null);

            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.updateAdjust(UUID.randomUUID(), saveUpdateAdjustDTO));
        }

        @Test
        @DisplayName("updateAdjust throws IllegalArgumentException when adjust cost is lesser than 0.01 cent")
        void updateAdjust_ThrowsIllegalArgumentException_WhenAdjustCostIsLesserThan1Cent() {

            this.mockRepositoryFindByIdAndIsActiveTrue(createValidAdjust());

            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setCost(0.0);

            assertThrows(IllegalArgumentException.class,
                    () -> adjustService.updateAdjust(UUID.randomUUID(), saveUpdateAdjustDTO));
        }
    }

    @DisplayName("Test deleteAdjust method")
    @Nested
    class DeleteAdjustTest {

        private void mockRepositoryExistsByIdAndIsActiveTrue(Boolean existsOrIsActive) {
            BDDMockito.when(adjustRepositoryMock.existsByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(existsOrIsActive);
        }

        @Test
        @DisplayName("deleteAdjust inactivates adjust when successful")
        void deleteAdjust_InactivatesAdjust_WhenSuccessful() {

            this.mockRepositoryExistsByIdAndIsActiveTrue(true);

            assertDoesNotThrow(() -> adjustService.deleteAdjust(UUID.randomUUID()));
        }

        @Test
        @DisplayName("deleteAdjust throws EntityNotFoundException when adjust does not exist or is already inactive")
        void deleteAdjust_ThrowsEntityNotFoundException_WhenAdjustDoesNotExistOrIsAlreadyInactive() {

            this.mockRepositoryExistsByIdAndIsActiveTrue(false);

            assertThrows(EntityNotFoundException.class,
                    () -> adjustService.deleteAdjust(UUID.randomUUID()));
        }

        @Test
        @DisplayName("deleteAdjust throws EntityNotFoundException when adjust id is null")
        void deleteAdjust_ThrowsEntityNotFoundException_WhenAdjustIdIsNull() {

            assertThrows(EntityNotFoundException.class,
                    () -> adjustService.deleteAdjust(null));
        }
    }
}
