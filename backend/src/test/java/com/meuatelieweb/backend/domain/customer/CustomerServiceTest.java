package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.*;
import static com.meuatelieweb.backend.domain.customer.CustomerCreator.createValidSaveCustomerDTO;
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
    private CustomerRepository customerRepositoryMock;

    @Mock
    private CustomerConverter customerConverterMock;

    private void mockConverterToCustomerDTO(CustomerDTO customerDTO) {
        when(customerConverterMock.toCustomerDTO(any(Customer.class))).thenReturn(customerDTO);
    }

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {

        private void mockRepositoryFindAll(PageImpl<Customer> pageable) {
            when(customerRepositoryMock.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(pageable);
        }

        @Test
        @DisplayName("findAll returns page of customers when successful")
        void findAll_ReturnsPageOfCustomers_WhenSuccessful() {
            List<Customer> validCustomers = List.of(createValidCustomer());
            PageImpl<Customer> pageable = new PageImpl<>(validCustomers, Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            CustomerDTO customerDTO = createValidCustomerDTO(validCustomers.get(0).getId());

            mockConverterToCustomerDTO(customerDTO);

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

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {
        
        private void mockRepositoryFindById(){
            BDDMockito.when(customerRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.of(createValidCustomer()));
        }

        @Test
        @DisplayName("findById returns customer when successful")
        void findById_ReturnsCustomer_WhenSuccessful() {
            CustomerDTO customerDTO = createValidCustomerDTO(UUID.randomUUID());

            this.mockRepositoryFindById();

            mockConverterToCustomerDTO(customerDTO);

            CustomerDTO customerFound = customerService.findById(UUID.randomUUID());

            assertNotNull(customerFound);
            assertEquals(customerDTO, customerFound);
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when customer is not found")
        void findById_ThrowsEntityNotFoundException_WhenCustomerIsNotFound() {
            BDDMockito.when(customerRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> customerService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when id customer is null")
        void findById_ThrowsEntityNotFoundException_WhenIdCustomerIsNull() {
            BDDMockito.when(customerRepositoryMock.findById(null))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> customerService.findById(null));
        }
    }

    @DisplayName("Test addCustomer method")
    @Nested
    class AddCustomerTests {

        private void mockRepositorySave(){
            BDDMockito.when(customerRepositoryMock.save(any(Customer.class)))
                    .thenReturn(createValidCustomer());
        }

        @Test
        @DisplayName("addCustomer returns customer when successful")
        void addCustomer_ReturnsCustomer_WhenSuccessful() {
            CustomerDTO customerDTO = createValidCustomerDTO(createValidCustomer().getId());

            this.mockRepositorySave();

            mockConverterToCustomerDTO(customerDTO);

            CustomerDTO customerSaved = customerService.addCustomer(createValidSaveCustomerDTO());

            assertNotNull(customerSaved);
            assertEquals(customerDTO, customerSaved);
        }

        @Test
        @DisplayName("addCustomer throws IllegalArgumentException when customer is null")
        void addCustomer_ThrowsIllegalArgumentException_WhenCustomerIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerService.addCustomer(null));
        }

        @Test
        @DisplayName("addCustomer throws IllegalArgumentException when customer name is null")
        void addCustomer_ThrowsIllegalArgumentException_WhenCustomerNameIsNull() {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            saveCustomerDTO.setName(null);

            assertThrows(IllegalArgumentException.class,
                    () -> customerService.addCustomer(saveCustomerDTO));
        }

        @Test
        @DisplayName("addCustomer throws IllegalArgumentException when customer email is null")
        void addCustomer_ThrowsIllegalArgumentException_WhenCustomerEmailIsNull() {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            saveCustomerDTO.setEmail(null);

            assertThrows(IllegalArgumentException.class,
                    () -> customerService.addCustomer(saveCustomerDTO));
        }

        @Test
        @DisplayName("addCustomer throws DuplicateKeyException when customer email already exists")
        void addCustomer_ThrowsDuplicateKeyException_WhenCustomerEmailAlreadyExists() {
            BDDMockito.when(customerRepositoryMock.existsByEmail(anyString())).thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> customerService.addCustomer(createValidSaveCustomerDTO()));
        }

        @Test
        @DisplayName("addCustomer throws IllegalArgumentException when customer phone is not valid")
        void addCustomer_ThrowsIllegalArgumentException_WhenCustomerPhoneIsNotValid() {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            saveCustomerDTO.setPhone("abc123");

            assertThrows(IllegalArgumentException.class,
                    () -> customerService.addCustomer(saveCustomerDTO));
        }

        @Test
        @DisplayName("addCustomer throws DuplicateKeyException when customer phone already exists")
        void addCustomer_ThrowsDuplicateKeyException_WhenCustomerPhoneAlreadyExists() {
            BDDMockito.when(customerRepositoryMock.existsByPhone(anyString())).thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> customerService.addCustomer(createValidSaveCustomerDTO()));
        }
    }
}
