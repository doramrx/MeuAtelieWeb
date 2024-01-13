package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Service")
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerConverter customerConverter;

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {

        private void mockRepositoryFindAll(PageImpl<Customer> pageable) {
            when(customerRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(pageable);
        }

        private void mockConverterToCustomerDTO(CustomerDTO customerDTO, Customer validCustomers) {
            when(customerConverter.toCustomerDTO(validCustomers)).thenReturn(customerDTO);
        }

        @Test
        @DisplayName("findAll returns page of customers when successful")
        void findAll_ReturnsPageOfCustomers_WhenSuccessful() {
            List<Customer> validCustomers = List.of(createValidCustomer());
            PageImpl<Customer> pageable = new PageImpl<>(validCustomers, Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            CustomerDTO customerDTO = createValidCustomerDTO(validCustomers.get(0).getId());

            this.mockConverterToCustomerDTO(customerDTO, validCustomers.get(0));

            List<CustomerDTO> customerList = customerService.findAll(
                    pageable.getPageable(), null, null, null, null
            ).toList();

            assertNotNull(customerList);
            assertFalse(customerList.isEmpty());
            assertEquals(1, customerList.size());
            assertEquals(customerDTO, customerList.get(0));
        }

        @Test
        @DisplayName("findAll returns an empty page of customers when customers do not exist")
        void findAll_ReturnsEmptyPageOfCustomers_WhenCustomersDoNotExist() {
            PageImpl<Customer> pageable = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<CustomerDTO> customerList = customerService.findAll(
                    pageable.getPageable(), null, null, null, null
            ).toList();

            assertNotNull(customerList);
            assertTrue(customerList.isEmpty());
        }
    }
}
