package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
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

import static com.meuatelieweb.backend.util.CustomerCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Service")
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepositoryMock;

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

            List<Customer> customerList = customerService.findAll(
                    pageable.getPageable(), null, null, null, null
            ).toList();

            assertNotNull(customerList);
            assertFalse(customerList.isEmpty());
            assertEquals(1, customerList.size());
            assertEquals(validCustomers.get(0), customerList.get(0));
        }

        @Test
        @DisplayName("findAll returns an empty page of customers when customers do not exist")
        void findAll_ReturnsEmptyPageOfCustomers_WhenCustomersDoNotExist() {
            PageImpl<Customer> pageable = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<Customer> customers = customerService.findAll(
                    pageable.getPageable(), null, null, null, null
            ).toList();

            assertNotNull(customers);
            assertTrue(customers.isEmpty());
        }
    }

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(Customer customer) {
            BDDMockito.when(customerRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customer));
        }

        @Test
        @DisplayName("findById returns customer when successful")
        void findById_ReturnsCustomer_WhenSuccessful() {
            Customer customer = createValidCustomer();

            this.mockRepositoryFindById(customer);

            Customer customerFound = customerService.findById(UUID.randomUUID());

            assertNotNull(customerFound);
            assertEquals(customer.getId(), customerFound.getId());
            assertEquals(customer.getName(), customerFound.getName());
            assertEquals(customer.getEmail(), customerFound.getEmail());
            assertEquals(customer.getPhone(), customerFound.getPhone());
            assertEquals(customer.getIsActive(), customerFound.getIsActive());
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when customer is not found")
        void findById_ThrowsEntityNotFoundException_WhenCustomerIsNotFound() {
            this.mockRepositoryFindById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws IllegalArgumentException when id customer is null")
        void findById_ThrowsIllegalArgumentException_WhenIdCustomerIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerService.findById(null));
        }
    }

    @DisplayName("Test findByIdAndIsActiveTrue method")
    @Nested
    class FindByIdAndIsActiveTrueTests {

        private void mockRepositoryFindByIdAndIsActiveTrue(Customer customer) {
            BDDMockito.when(customerRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customer));
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue returns customer when successful")
        void findByIdAndIsActiveTrue_ReturnsCustomer_WhenSuccessful() {
            Customer customer = createValidCustomer();

            this.mockRepositoryFindByIdAndIsActiveTrue(customer);

            Customer customerFound = customerService.findByIdAndIsActiveTrue(UUID.randomUUID());

            assertNotNull(customerFound);
            assertEquals(customer.getId(), customerFound.getId());
            assertEquals(customer.getName(), customerFound.getName());
            assertEquals(customer.getEmail(), customerFound.getEmail());
            assertEquals(customer.getPhone(), customerFound.getPhone());
            assertEquals(customer.getIsActive(), customerFound.getIsActive());
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue throws EntityNotFoundException when customer is not found")
        void findByIdAndIsActiveTrue_ThrowsEntityNotFoundException_WhenCustomerIsNotFound() {
            this.mockRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerService.findByIdAndIsActiveTrue(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue throws IllegalArgumentException when id customer is null")
        void findByIdAndIsActiveTrue_ThrowsIllegalArgumentException_WhenIdCustomerIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerService.findByIdAndIsActiveTrue(null));
        }
    }

    @DisplayName("Test addCustomer method")
    @Nested
    class AddCustomerTests {

        @Test
        @DisplayName("addCustomer returns customer when successful")
        void addCustomer_ReturnsCustomer_WhenSuccessful() {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);

            customerService.addCustomer(createValidSaveCustomerDTO());
            verify(customerRepositoryMock, atMostOnce()).save(argumentCaptor.capture());

            Customer customerSaved = argumentCaptor.getValue();

            assertNotNull(customerSaved);
            assertEquals(saveCustomerDTO.getName(), customerSaved.getName());
            assertEquals(saveCustomerDTO.getEmail(), customerSaved.getEmail());
            assertEquals(saveCustomerDTO.getPhone(), customerSaved.getPhone());
            assertTrue(customerSaved.getIsActive());
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

    @DisplayName("Test updateCustomer method")
    @Nested
    class UpdateCustomerTests {

        private void mockRepositoryFindByIdAndIsActiveTrue(Customer customer) {
            BDDMockito.when(customerRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customer));
        }

        @Test
        @DisplayName("updateCustomer updates customer when successful")
        void updateCustomer_UpdatesCustomer_WhenSuccessful() {
            UpdateCustomerDTO validUpdateCustomerDTO = createValidUpdateCustomerDTO();
            Customer customerSpy = spy(createValidCustomer());

            this.mockRepositoryFindByIdAndIsActiveTrue(customerSpy);

            customerService.updateCustomer(
                    UUID.randomUUID(), validUpdateCustomerDTO
            );

            verify(customerSpy, atLeastOnce()).setName(validUpdateCustomerDTO.getName());
            verify(customerSpy, atLeastOnce()).setPhone(validUpdateCustomerDTO.getPhone());
            verify(customerRepositoryMock, atMostOnce()).save(any(Customer.class));
        }

        @Test
        @DisplayName("updateCustomer throws IllegalArgumentException when customer is null")
        void updateCustomer_ThrowsIllegalArgumentException_WhenCustomerIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), null));
        }

        @Test
        @DisplayName("updateCustomer throws IllegalArgumentException when customer id is null")
        void updateCustomer_ThrowsIllegalArgumentException_WhenCustomerIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> customerService.updateCustomer(null, createValidUpdateCustomerDTO()));
        }

        @Test
        @DisplayName("updateCustomer throws EntityNotFoundException when customer does not exist")
        void updateCustomer_ThrowsEntityNotFoundException_WhenCustomerDoesNotExist() {

            this.mockRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), createValidUpdateCustomerDTO()));
        }

        @Test
        @DisplayName("updateCustomer throws DuplicateKeyException when phone already exists")
        void updateCustomer_ThrowsDuplicateKeyException_WhenPhoneAlreadyExists() {

            this.mockRepositoryFindByIdAndIsActiveTrue(createValidCustomer());

            BDDMockito.when(customerRepositoryMock.existsByPhoneAndIdNot(anyString(), any(UUID.class)))
                    .thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), createValidUpdateCustomerDTO()));
        }

        @Test
        @DisplayName("updateCustomer throws IllegalArgumentException when phone is not valid")
        void updateCustomer_ThrowsIllegalArgumentException_WhenPhoneIsNotValid() {

            this.mockRepositoryFindByIdAndIsActiveTrue(createValidCustomer());

            UpdateCustomerDTO updateCustomerDTO = createValidUpdateCustomerDTO();
            updateCustomerDTO.setPhone("abc123");

            assertThrows(IllegalArgumentException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), updateCustomerDTO));
        }
    }

    @DisplayName("Test deleteCustomer method")
    @Nested
    class DeleteCustomerTest {

        private void mockRepositoryExistsByIdAndIsActiveTrue(Boolean existsOrIsActive) {
            BDDMockito.when(customerRepositoryMock.existsByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(existsOrIsActive);
        }

        @Test
        @DisplayName("deleteCustomer inactivates customer when successful")
        void deleteCustomer_InactivatesCustomer_WhenSuccessful() {

            this.mockRepositoryExistsByIdAndIsActiveTrue(true);

            assertDoesNotThrow(() -> customerService.deleteCustomer(UUID.randomUUID()));
        }

        @Test
        @DisplayName("deleteCustomer throws EntityNotFoundException when customer does not exist or is already inactive")
        void deleteCustomer_ThrowsEntityNotFoundException_WhenCustomerDoesNotExistOrIsAlreadyInactive() {

            this.mockRepositoryExistsByIdAndIsActiveTrue(false);

            assertThrows(EntityNotFoundException.class,
                    () -> customerService.deleteCustomer(UUID.randomUUID()));
        }

        @Test
        @DisplayName("deleteCustomer throws IllegalArgumentException when customer id is null")
        void deleteCustomer_ThrowsIllegalArgumentException_WhenCustomerIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> customerService.deleteCustomer(null));
        }

    }
}
