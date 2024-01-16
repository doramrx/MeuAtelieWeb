package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
            assertEquals(customerDTO.getId(), customerFound.getId());
            assertEquals(customerDTO.getName(), customerFound.getName());
            assertEquals(customerDTO.getEmail(), customerFound.getEmail());
            assertEquals(customerDTO.getPhone(), customerFound.getPhone());
            assertEquals(customerDTO.getIsActive(), customerFound.getIsActive());
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
        @DisplayName("findById throws IllegalArgumentException when id customer is null")
        void findById_ThrowsIllegalArgumentException_WhenIdCustomerIsNull() {
            assertThrows(IllegalArgumentException.class,
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

    @DisplayName("Test updateCustomer method")
    @Nested
    class UpdateCustomerTests {

        private void mockRepositoryFindByIdAndIsActiveTrue() {
            BDDMockito.when(customerRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.of(createValidCustomer()));
        }

        private void mockRepositorySave(){
            BDDMockito.when(customerRepositoryMock.save(any(Customer.class)))
                    .thenReturn(createValidCustomer());
        }

        @Test
        @DisplayName("updateCustomer updates customer when successful")
        void updateCustomer_UpdatesCustomer_WhenSuccessful() {
            CustomerDTO customerDTO = createValidCustomerDTO(createValidCustomer().getId());

            this.mockRepositoryFindByIdAndIsActiveTrue();
            this.mockRepositorySave();

            mockConverterToCustomerDTO(customerDTO);

            CustomerDTO customerUpdated = customerService.updateCustomer(UUID.randomUUID(), createValidUpdateCustomerDTO());

            assertNotNull(customerUpdated);
            assertEquals(customerDTO.getId(), customerUpdated.getId());
            assertEquals(customerDTO.getName(), customerUpdated.getName());
            assertEquals(customerDTO.getEmail(), customerUpdated.getEmail());
            assertEquals(customerDTO.getPhone(), customerUpdated.getPhone());
            assertEquals(customerDTO.getIsActive(), customerUpdated.getIsActive());
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
            BDDMockito.when(customerRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), createValidUpdateCustomerDTO()));
        }

        @Test
        @DisplayName("updateCustomer throws DuplicateKeyException when phone already exists")
        void updateCustomer_ThrowsDuplicateKeyException_WhenPhoneAlreadyExists() {

            this.mockRepositoryFindByIdAndIsActiveTrue();

            BDDMockito.when(customerRepositoryMock.existsByPhoneAndIdNot(anyString(), any(UUID.class)))
                    .thenReturn(true);

            assertThrows(DuplicateKeyException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), createValidUpdateCustomerDTO()));
        }

        @Test
        @DisplayName("updateCustomer throws IllegalArgumentException when phone is not valid")
        void updateCustomer_ThrowsIllegalArgumentException_WhenPhoneIsNotValid() {

            this.mockRepositoryFindByIdAndIsActiveTrue();

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
