package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.adjust.Adjust;
import com.meuatelieweb.backend.domain.adjust.AdjustConverter;
import com.meuatelieweb.backend.domain.customeradjust.dto.CustomerAdjustDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.meuatelieweb.backend.util.AdjustCreator.createValidAdjust;
import static com.meuatelieweb.backend.util.AdjustCreator.createValidAdjustDTO;
import static com.meuatelieweb.backend.util.CustomerAdjustCreator.createValidCustomerAdjust;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Adjust Converter")
class CustomerAdjustConverterTest {

    @InjectMocks
    private CustomerAdjustConverter customerAdjustConverter;

    @Mock
    private AdjustConverter adjustConverterMock;

    private void mockAdjustConverterToAdjustDTO(UUID adjustId){
        BDDMockito.when(adjustConverterMock.toAdjustDTO(any(Adjust.class)))
                .thenReturn(createValidAdjustDTO(adjustId));
    }

    @Test
    @DisplayName("toCustomerAdjustDTO returns CustomerAdjustDTO when successful")
    void toCustomerAdjustDTO_ReturnsCustomerAdjustDTO_WhenSuccessful() {
        CustomerAdjust customerAdjust = createValidCustomerAdjust();
        Adjust adjust = createValidAdjust();
        customerAdjust.setAdjust(adjust);
        this.mockAdjustConverterToAdjustDTO(adjust.getId());

        CustomerAdjustDTO customerAdjustDTO = customerAdjustConverter.toCustomerAdjustDTO(customerAdjust);

        assertNotNull(customerAdjustDTO);
        assertEquals(customerAdjust.getId(), customerAdjustDTO.getId());
        assertEquals(customerAdjust.getAdjust().getId(), customerAdjustDTO.getAdjust().getId());
        assertEquals(customerAdjust.getAdjust().getName(), customerAdjustDTO.getAdjust().getName());
        assertEquals(customerAdjust.getAdjust().getCost(), customerAdjustDTO.getAdjust().getCost());
        assertEquals(customerAdjust.getAdjustmentCost(), customerAdjustDTO.getAdjustmentCost());
        assertEquals(customerAdjust.getIsActive(), customerAdjustDTO.getIsActive());
    }

    @Test
    @DisplayName("toCustomerAdjustDTO throws IllegalArgumentException when customer adjust is null")
    void toCustomerAdjustDTO_ThrowsIllegalArgumentException_WhenCustomerAdjustIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> customerAdjustConverter.toCustomerAdjustDTO(null));
    }
}
