package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.ListOrderCustomerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomer;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Converter")
class CustomerConverterTest {

    @InjectMocks
    private CustomerConverter customerConverter;

    @Test
    @DisplayName("toCustomerDTO returns CustomerDTO when successful")
    void toCustomerDTO_ReturnsCustomerDTO_WhenSuccessful() {
        Customer customer = createValidCustomer();

        CustomerDTO customerDTO = customerConverter.toCustomerDTO(customer);

        assertNotNull(customerDTO);
        assertEquals(customer.getId(), customerDTO.getId());
        assertEquals(customer.getName(), customerDTO.getName());
        assertEquals(customer.getEmail(), customerDTO.getEmail());
        assertEquals(customer.getPhone(), customerDTO.getPhone());
        assertEquals(customer.getIsActive(), customerDTO.getIsActive());
    }

    @Test
    @DisplayName("toCustomerDTO throws IllegalArgumentException when customer is null")
    void toCustomerDTO_ThrowsIllegalArgumentException_WhenCustomerIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> customerConverter.toCustomerDTO(null));
    }

    @Test
    @DisplayName("toListOrderCustomerDTO returns ListOrderCustomerDTO when successful")
    void toListOrderCustomerDTO_ReturnsListOrderCustomerDTO_WhenSuccessful() {
        Customer customer = createValidCustomer();

        ListOrderCustomerDTO listOrderCustomerDTO = customerConverter.toListOrderCustomerDTO(customer);

        assertNotNull(listOrderCustomerDTO);
        assertEquals(customer.getName(), listOrderCustomerDTO.getName());
        assertEquals(customer.getEmail(), listOrderCustomerDTO.getEmail());
    }

    @Test
    @DisplayName("toListOrderCustomerDTO throws IllegalArgumentException when customer is null")
    void toListOrderCustomerDTO_ThrowsIllegalArgumentException_WhenCustomerIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> customerConverter.toListOrderCustomerDTO(null));
    }
}
