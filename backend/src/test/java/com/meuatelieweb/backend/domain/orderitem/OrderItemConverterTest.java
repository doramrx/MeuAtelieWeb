package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjustConverter;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasureConverter;
import com.meuatelieweb.backend.domain.orderitem.dto.OrderItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.meuatelieweb.backend.util.CustomerAdjustCreator.createValidCustomerAdjust;
import static com.meuatelieweb.backend.util.CustomerAdjustCreator.createValidCustomerAdjustDTO;
import static com.meuatelieweb.backend.util.CustomerMeasureCreator.createValidCustomerMeasure;
import static com.meuatelieweb.backend.util.CustomerMeasureCreator.createValidCustomerMeasureDTO;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidAdjustOrderItem;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidTailoredOrderItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Order Item Converter")
class OrderItemConverterTest {

    @InjectMocks
    private OrderItemConverter orderItemConverter;

    @Mock
    private CustomerAdjustConverter customerAdjustConverterMock;

    @Mock
    private CustomerMeasureConverter customerMeasureConverterMock;

    private void mockCustomerAdjustConverterToCustomerAdjustDTO(UUID customerAdjustId) {
        BDDMockito.when(customerAdjustConverterMock.toCustomerAdjustDTO(any(CustomerAdjust.class)))
                .thenReturn(createValidCustomerAdjustDTO(customerAdjustId));
    }

    private void mockCustomerMeasureConverterToCustomerMeasureDTO(UUID customerMeasureId) {
        BDDMockito.when(customerMeasureConverterMock.toCustomerMeasureDTO(any(CustomerMeasure.class)))
                .thenReturn(createValidCustomerMeasureDTO(customerMeasureId));
    }

    @Test
    @DisplayName("toOrderItemDTO returns OrderItemDTO of type 'ADJUST' when successful")
    void toOrderItemDTO_ReturnsOrderItemDTOOfTypeAdjust_WhenSuccessful() {
        OrderItem orderItem = createValidAdjustOrderItem();

        CustomerAdjust customerAdjust = createValidCustomerAdjust();
        ((AdjustOrderItem) orderItem).setCustomerAdjustments(List.of(customerAdjust));
        this.mockCustomerAdjustConverterToCustomerAdjustDTO(customerAdjust.getId());

        OrderItemDTO orderItemDTO = orderItemConverter.toOrderItemDTO(orderItem);

        assertNotNull(orderItemDTO);
        assertEquals(orderItem.getId(), orderItemDTO.getId());
        assertEquals(orderItem.getCost(), orderItemDTO.getCost());
        assertEquals(orderItem.getTitle(), orderItemDTO.getTitle());
        assertEquals(orderItem.getDescription(), orderItemDTO.getDescription());
        assertEquals(orderItem.getCreatedAt(), orderItemDTO.getCreatedAt());
        assertEquals(orderItem.getDueDate(), orderItemDTO.getDueDate());
        assertEquals(orderItem.getDeliveredAt(), orderItemDTO.getDeliveredAt());
        assertEquals(orderItem.getIsActive(), orderItemDTO.getIsActive());
    }

    @Test
    @DisplayName("toOrderItemDTO returns OrderItemDTO of type 'TAILORED' when successful")
    void toOrderItemDTO_ReturnsOrderItemDTOOfTypeTailored_WhenSuccessful() {
        OrderItem orderItem = createValidTailoredOrderItem();

        CustomerMeasure customerMeasure = createValidCustomerMeasure();
        ((TailoredOrderItem) orderItem).setCustomerMeasures(List.of(customerMeasure));
        this.mockCustomerMeasureConverterToCustomerMeasureDTO(customerMeasure.getId());

        OrderItemDTO orderItemDTO = orderItemConverter.toOrderItemDTO(orderItem);

        assertNotNull(orderItemDTO);
        assertEquals(orderItem.getId(), orderItemDTO.getId());
        assertEquals(orderItem.getCost(), orderItemDTO.getCost());
        assertEquals(orderItem.getTitle(), orderItemDTO.getTitle());
        assertEquals(orderItem.getDescription(), orderItemDTO.getDescription());
        assertEquals(orderItem.getCreatedAt(), orderItemDTO.getCreatedAt());
        assertEquals(orderItem.getDueDate(), orderItemDTO.getDueDate());
        assertEquals(orderItem.getDeliveredAt(), orderItemDTO.getDeliveredAt());
        assertEquals(orderItem.getIsActive(), orderItemDTO.getIsActive());
    }

    @Test
    @DisplayName("toOrderItemDTO throws IllegalArgumentException when order item is null")
    void toOrderItemDTO_ThrowsIllegalArgumentException_WhenOrderItemIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> orderItemConverter.toOrderItemDTO(null));
    }
}
