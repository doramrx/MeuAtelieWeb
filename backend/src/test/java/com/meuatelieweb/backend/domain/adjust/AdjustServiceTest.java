package com.meuatelieweb.backend.domain.adjust;

import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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
}
