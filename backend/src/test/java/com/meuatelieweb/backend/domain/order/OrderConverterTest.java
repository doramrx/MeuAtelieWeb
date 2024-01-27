package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.CustomerConverter;
import com.meuatelieweb.backend.domain.order.dto.ListOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.OrderDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderItemConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.meuatelieweb.backend.util.CustomerCreator.*;
import static com.meuatelieweb.backend.util.OrderCreator.createValidOrder;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidOrderItem;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidOrderItemDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Order Converter")
class OrderConverterTest {

    @InjectMocks
    private OrderConverter orderConverter;

    @Mock
    private CustomerConverter customerConverterMock;

    @Mock
    private OrderItemConverter orderItemConverterMock;

    private void mockCustomerConverterToCustomerDTO(UUID customerId) {
        BDDMockito.when(customerConverterMock.toCustomerDTO(any(Customer.class)))
                .thenReturn(createValidCustomerDTO(customerId));
    }

    private void mockCustomerConverterToListOrderCustomerDTO(Customer customer) {
        BDDMockito.when(customerConverterMock.toListOrderCustomerDTO(any(Customer.class)))
                .thenReturn(createValidListOrderCustomerDTO(customer));
    }

    private void mockOrderItemConverterToOrderItemDTO(UUID itemId) {
        BDDMockito.when(orderItemConverterMock.toOrderItemDTO(any(OrderItem.class)))
                .thenReturn(createValidOrderItemDTO(itemId));
    }

    @Test
    @DisplayName("toOrderDTO returns OrderDTO when successful")
    void toOrderDTO_ReturnsOrderDTO_WhenSuccessful() {
        Order order = createValidOrder();

        this.mockCustomerConverterToCustomerDTO(createValidCustomer().getId());
        this.mockOrderItemConverterToOrderItemDTO(createValidOrderItem().getId());

        OrderDTO orderDTO = orderConverter.toOrderDTO(order);

        assertNotNull(orderDTO);
        assertEquals(order.getId(), orderDTO.getId());
        assertEquals(order.getOrderNumber(), orderDTO.getOrderNumber());
        assertEquals(order.getCreatedAt(), orderDTO.getCreatedAt());
        assertEquals(order.getUpdatedAt(), orderDTO.getUpdatedAt());
        assertEquals(order.getFinishedAt(), orderDTO.getFinishedAt());
        assertEquals(order.getIsActive(), orderDTO.getIsActive());
    }

    @Test
    @DisplayName("toOrderDTO throws IllegalArgumentException when order is null")
    void toOrderDTO_ThrowsIllegalArgumentException_WhenOrderIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> orderConverter.toOrderDTO(null));
    }

    @Test
    @DisplayName("toListOrderDTO returns ListOrderDTO when successful")
    void toListOrderDTO_ReturnsListOrderDTO_WhenSuccessful() {
        Order order = createValidOrder();

        this.mockCustomerConverterToListOrderCustomerDTO(createValidCustomer());

        ListOrderDTO listOrderDTO = orderConverter.toListOrderDTO(order);

        assertNotNull(listOrderDTO);
        assertEquals(order.getId(), listOrderDTO.getId());
        assertEquals(order.getOrderNumber(), listOrderDTO.getOrderNumber());
        assertEquals(order.getCreatedAt(), listOrderDTO.getCreatedAt());
        assertEquals(order.getUpdatedAt(), listOrderDTO.getUpdatedAt());
        assertEquals(order.getFinishedAt(), listOrderDTO.getFinishedAt());
        assertEquals(order.getIsActive(), listOrderDTO.getIsActive());
    }

    @Test
    @DisplayName("toListOrderDTO throws IllegalArgumentException when order is null")
    void toListOrderDTO_ThrowsIllegalArgumentException_WhenOrderIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> orderConverter.toListOrderDTO(null));
    }
}
