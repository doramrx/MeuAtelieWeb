package com.meuatelieweb.backend.domain.adjust;

import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import com.meuatelieweb.backend.domain.adjust.dto.SaveUpdateAdjustDTO;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Adjust Service")
class AdjustServiceTest {

    @InjectMocks
    private AdjustService adjustService;

    @Mock
    private AdjustRepository adjustRepositoryMock;

    @Mock
    private AdjustConverter adjustConverterMock;

    private void mockConverterToAdjustDTO(AdjustDTO adjustDTO){
        BDDMockito.when(adjustConverterMock.toAdjustDTO(any(Adjust.class))).thenReturn(adjustDTO);
    }

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

            AdjustDTO adjustDTO = createValidAdjustDTO(validAdjusts.get(0).getId());

            mockConverterToAdjustDTO(adjustDTO);

            List<AdjustDTO> adjustList = adjustService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(adjustList);
            assertFalse(adjustList.isEmpty());
            assertEquals(1, adjustList.size());
            assertEquals(adjustDTO, adjustList.get(0));
        }

        @Test
        @DisplayName("findAll returns an empty page of adjusts when adjusts do not exist")
        void findAll_ReturnsEmptyPageOfAdjusts_WhenAdjustsDoNotExist() {
            PageImpl<Adjust> pageable = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<AdjustDTO> adjustList = adjustService.findAll(
                    pageable.getPageable(), null, null
            ).toList();

            assertNotNull(adjustList);
            assertTrue(adjustList.isEmpty());
        }
    }

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(){
            BDDMockito.when(adjustRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.of(createValidAdjust()));
        }

        @Test
        @DisplayName("findById returns adjust when successful")
        void findById_ReturnsAdjust_WhenSuccessful() {
            AdjustDTO adjustDTO = createValidAdjustDTO(UUID.randomUUID());

            this.mockRepositoryFindById();

            mockConverterToAdjustDTO(adjustDTO);

            AdjustDTO adjustFound = adjustService.findById(UUID.randomUUID());

            assertNotNull(adjustFound);
            assertEquals(adjustDTO.getId(), adjustFound.getId());
            assertEquals(adjustDTO.getName(), adjustFound.getName());
            assertEquals(adjustDTO.getCost(), adjustFound.getCost());
            assertEquals(adjustDTO.getIsActive(), adjustFound.getIsActive());
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when adjust is not found")
        void findById_ThrowsEntityNotFoundException_WhenAdjustIsNotFound() {
            BDDMockito.when(adjustRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> adjustService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when id adjust is null")
        void findById_ThrowsEntityNotFoundException_WhenIdAdjustIsNull() {
            assertThrows(EntityNotFoundException.class,
                    () -> adjustService.findById(null));
        }
    }

    @DisplayName("Test addAdjust method")
    @Nested
    class AddAdjustTests {

        private void mockRepositorySave(){
            BDDMockito.when(adjustRepositoryMock.save(any(Adjust.class)))
                    .thenReturn(createValidAdjust());
        }

        @Test
        @DisplayName("addAdjust returns adjust when successful")
        void addAdjust_ReturnsAdjust_WhenSuccessful() {
            AdjustDTO adjustDTO = createValidAdjustDTO(createValidAdjust().getId());

            this.mockRepositorySave();

            mockConverterToAdjustDTO(adjustDTO);

            AdjustDTO adjustSaved = adjustService.addAdjust(createValidSaveUpdateAdjustDTO());

            assertNotNull(adjustSaved);
            assertEquals(adjustDTO, adjustSaved);
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
}
