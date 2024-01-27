package com.meuatelieweb.backend.domain.adjust;

import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.meuatelieweb.backend.util.AdjustCreator.createValidAdjust;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Adjust Converter")
class AdjustConverterTest {
    
    @InjectMocks
    private AdjustConverter adjustConverter;

    @Test
    @DisplayName("toAdjustDTO returns AdjustDTO when successful")
    void toAdjustDTO_ReturnsAdjustDTO_WhenSuccessful() {
        Adjust adjust = createValidAdjust();

        AdjustDTO adjustDTO = adjustConverter.toAdjustDTO(adjust);

        assertNotNull(adjustDTO);
        assertEquals(adjust.getId(), adjustDTO.getId());
        assertEquals(adjust.getName(), adjustDTO.getName());
        assertEquals(adjust.getCost(), adjustDTO.getCost());
        assertEquals(adjust.getIsActive(), adjustDTO.getIsActive());
    }

    @Test
    @DisplayName("toAdjustDTO throws IllegalArgumentException when adjust is null")
    void toAdjustDTO_ThrowsIllegalArgumentException_WhenAdjustIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> adjustConverter.toAdjustDTO(null));
    }
}
