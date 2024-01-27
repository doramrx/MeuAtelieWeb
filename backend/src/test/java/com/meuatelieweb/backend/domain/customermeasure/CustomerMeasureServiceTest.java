package com.meuatelieweb.backend.domain.customermeasure;

import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.measure.MeasureService;
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

import java.util.*;

import static com.meuatelieweb.backend.util.CustomerMeasureCreator.*;
import static com.meuatelieweb.backend.util.MeasureCreator.createValidMeasure;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidTailoredOrderItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Measure Service")
class CustomerMeasureServiceTest {

    @InjectMocks
    private CustomerMeasureService customerMeasureService;

    @Mock
    private CustomerMeasureRepository customerMeasureRepositoryMock;

    @Mock
    private MeasureService measureServiceMock;

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(CustomerMeasure customerMeasure) {
            BDDMockito.when(customerMeasureRepositoryMock.findCustomerMeasureById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customerMeasure));
        }

        @Test
        @DisplayName("findById returns customer measure when successful")
        void findById_ReturnsCustomerMeasure_WhenSuccessful() {
            CustomerMeasure customerMeasure = createValidCustomerMeasure();

            this.mockRepositoryFindById(customerMeasure);

            CustomerMeasure customerMeasureFound = customerMeasureService.findById(UUID.randomUUID());

            assertNotNull(customerMeasureFound);
            assertEquals(customerMeasure.getId(), customerMeasureFound.getId());
            assertEquals(customerMeasure.getMeasurementValue(), customerMeasureFound.getMeasurementValue());
            assertEquals(customerMeasure.getMeasure(), customerMeasureFound.getMeasure());
            assertEquals(customerMeasure.getOrderItem(), customerMeasureFound.getOrderItem());
            assertEquals(customerMeasure.getIsActive(), customerMeasureFound.getIsActive());
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when customer measure is not found")
        void findById_ThrowsEntityNotFoundException_WhenCustomerMeasureIsNotFound() {
            this.mockRepositoryFindById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerMeasureService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws IllegalArgumentException when id customer measure is null")
        void findById_ThrowsIllegalArgumentException_WhenIdCustomerIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.findById(null));
        }
    }

    @DisplayName("Test addCustomerMeasures method")
    @Nested
    class AddCustomerMeasuresTests {

        private void mockMeasureServiceGetMeasures(Set<Measure> measures) {
            BDDMockito.when(measureServiceMock.getMeasures(any(Set.class)))
                    .thenReturn(measures);
        }

        @Test
        @DisplayName("addCustomerMeasures returns list of customer measures when successful")
        void addCustomerMeasures_ReturnsListOfCustomerMeasures_WhenSuccessful() {

            UUID measureId = UUID.fromString("322730d7-e8c7-4c60-afa4-e6bb875573f8");

            List<SaveCustomerMeasureDTO> saveCustomerMeasureList = List.of(createValidSaveCustomerMeasureDTO());
            saveCustomerMeasureList.get(0).setMeasurementId(measureId);

            Set<Measure> measures = Set.of(createValidMeasure());
            measures.iterator().next().setId(measureId);

            this.mockMeasureServiceGetMeasures(measures);

            ArgumentCaptor<List<CustomerMeasure>> argumentCaptor = ArgumentCaptor.forClass(List.class);

            customerMeasureService.addCustomerMeasures(createValidTailoredOrderItem(), saveCustomerMeasureList);
            verify(customerMeasureRepositoryMock, atMostOnce()).saveAllAndFlush(argumentCaptor.capture());

            List<CustomerMeasure> customerMeasuresSaved = argumentCaptor.getValue();
            CustomerMeasure customerMeasure = customerMeasuresSaved.get(0);

            assertNotNull(customerMeasure);
            assertEquals(saveCustomerMeasureList.get(0).getMeasurementId(), customerMeasure.getMeasure().getId());
            assertEquals(saveCustomerMeasureList.get(0).getMeasurementValue(), customerMeasure.getMeasurementValue());
            assertEquals(measures.iterator().next().getName(), customerMeasure.getMeasure().getName());
            assertTrue(customerMeasure.getIsActive());
        }

        @Test
        @DisplayName("addCustomerMeasures throws IllegalArgumentException when customer measure is null")
        void addCustomerMeasures_ThrowsIllegalArgumentException_WhenCustomerMeasureIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.addCustomerMeasures(createValidTailoredOrderItem(), null));
        }

        @Test
        @DisplayName("addCustomerMeasures throws IllegalArgumentException when item is null")
        void addCustomerMeasures_ThrowsIllegalArgumentException_WhenItemIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.addCustomerMeasures(
                            null, List.of(createValidSaveCustomerMeasureDTO())
                    )
            );
        }

        @Test
        @DisplayName("addCustomerMeasures throws IllegalArgumentException when measure value is null")
        void addCustomerMeasures_ThrowsIllegalArgumentException_WhenMeasureValueIsNull() {

            List<SaveCustomerMeasureDTO> saveCustomerMeasureList = List.of(createValidSaveCustomerMeasureDTO());
            saveCustomerMeasureList.get(0).setMeasurementValue(null);

            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.addCustomerMeasures(
                            createValidTailoredOrderItem(), saveCustomerMeasureList)
            );
        }

        @Test
        @DisplayName("addCustomerMeasures throws IllegalArgumentException when measure value is lesser than 0.01")
        void addCustomerMeasures_ThrowsIllegalArgumentException_WhenMeasureValueIsLesserThan1Cent() {

            List<SaveCustomerMeasureDTO> saveCustomerMeasureList = List.of(createValidSaveCustomerMeasureDTO());
            saveCustomerMeasureList.get(0).setMeasurementValue(0.0);

            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.addCustomerMeasures(
                            createValidTailoredOrderItem(), saveCustomerMeasureList)
            );
        }
    }

    @DisplayName("Test updateCustomerMeasure method")
    @Nested
    class UpdateCustomerMeasureTests {

        private void mockRepositoryFindCustomerMeasureById(CustomerMeasure customerMeasure) {
            BDDMockito.when(customerMeasureRepositoryMock.findCustomerMeasureById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customerMeasure));
        }

        @Test
        @DisplayName("updateCustomerMeasure updates customer measure when successful")
        void updateCustomerMeasure_UpdatesCustomerMeasure_WhenSuccessful() {
            UpdateCustomerMeasureDTO validUpdateCustomerMeasureDTO = createValidUpdateCustomerMeasureDTO();
            CustomerMeasure customerMeasureSpy = spy(createValidCustomerMeasure());

            this.mockRepositoryFindCustomerMeasureById(customerMeasureSpy);

            customerMeasureService.updateCustomerMeasure(
                    UUID.randomUUID(), validUpdateCustomerMeasureDTO
            );

            verify(customerMeasureSpy, atLeastOnce()).setMeasurementValue(validUpdateCustomerMeasureDTO.getMeasurementValue());
            verify(customerMeasureRepositoryMock, atMostOnce()).saveAndFlush(any(CustomerMeasure.class));
        }

        @Test
        @DisplayName("updateCustomerMeasure throws IllegalArgumentException when customerMeasure is null")
        void updateCustomerMeasure_ThrowsIllegalArgumentException_WhenCustomerMeasureIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.updateCustomerMeasure(UUID.randomUUID(), null));
        }

        @Test
        @DisplayName("updateCustomerMeasure throws IllegalArgumentException when customer measure id is null")
        void updateCustomerMeasure_ThrowsIllegalArgumentException_WhenCustomerMeasureIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.updateCustomerMeasure(null, createValidUpdateCustomerMeasureDTO()));
        }

        @Test
        @DisplayName("updateCustomerMeasure throws EntityNotFoundException when customer measure does not exist")
        void updateCustomerMeasure_ThrowsEntityNotFoundException_WhenCustomerMeasureDoesNotExist() {

            this.mockRepositoryFindCustomerMeasureById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerMeasureService.updateCustomerMeasure(UUID.randomUUID(), createValidUpdateCustomerMeasureDTO()));
        }

        @Test
        @DisplayName("updateCustomerMeasure throws IllegalArgumentException when customer measure value is lesser than 0.01")
        void updateCustomerMeasure_ThrowsIllegalArgumentException_WhenCustomerMeasureIsLesserThan1Cent() {

            UpdateCustomerMeasureDTO updateCustomerMeasureDTO = createValidUpdateCustomerMeasureDTO();
            updateCustomerMeasureDTO.setMeasurementValue(0.0);

            CustomerMeasure customerMeasureSpy = spy(createValidCustomerMeasure());
            this.mockRepositoryFindCustomerMeasureById(customerMeasureSpy);

            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.updateCustomerMeasure(UUID.randomUUID(), updateCustomerMeasureDTO));
        }
    }

    @DisplayName("Test deleteCustomerMeasures method")
    @Nested
    class DeleteCustomerMeasuresTests {

        private void mockRepositoryExistsByIdIn(Boolean exists, Set<UUID> uuidSet) {
            BDDMockito.when(customerMeasureRepositoryMock.existsByIdIn(uuidSet))
                    .thenReturn(exists);
        }

        @Test
        @DisplayName("deleteCustomerMeasures deletes customer measures when successful")
        void deleteCustomerMeasures_DeletesCustomerMeasures_WhenSuccessful() {

            Set<UUID> uuidSet = new HashSet<>();
            uuidSet.add(UUID.randomUUID());

            this.mockRepositoryExistsByIdIn(true, uuidSet);

            assertDoesNotThrow(() ->
                    customerMeasureService.deleteCustomerMeasures(uuidSet)
            );
        }

        @Test
        @DisplayName("deleteCustomerMeasures throws EntityNotFoundException when customer measures do not exist")
        void deleteCustomerMeasures_ThrowsEntityNotFoundException_WhenCustomerMeasuresDoNotExist() {
            Set<UUID> uuidSet = new HashSet<>();
            uuidSet.add(UUID.randomUUID());

            this.mockRepositoryExistsByIdIn(false, uuidSet);

            assertThrows(EntityNotFoundException.class,
                    () -> customerMeasureService.deleteCustomerMeasures(uuidSet)
            );
        }

        @Test
        @DisplayName("deleteCustomerMeasures throws IllegalArgumentException when customer measures id is null")
        void deleteCustomerMeasures_ThrowsIllegalArgumentException_WhenCustomerMeasuresIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> customerMeasureService.deleteCustomerMeasures(null));
        }
    }

    @DisplayName("Test singleDeleteCustomerMeasure method")
    @Nested
    class SingleDeleteCustomerMeasureTests {

        private void mockRepositoryFindCustomerMeasureById(CustomerMeasure customerMeasureSpy) {
            BDDMockito.when(customerMeasureRepositoryMock.findCustomerMeasureById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customerMeasureSpy));
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasure deletes customer measure when successful")
        void singleDeleteCustomerMeasure_DeletesCustomerMeasure_WhenSuccessful() {

            CustomerMeasure customerMeasureSpy = spy(createValidCustomerMeasure());
            this.mockRepositoryFindCustomerMeasureById(customerMeasureSpy);

            assertDoesNotThrow(() ->
                    customerMeasureService.singleDeleteCustomerMeasure(customerMeasureSpy.getId())
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasure throws EntityNotFoundException when customer measure does not exist")
        void singleDeleteCustomerMeasure_ThrowsEntityNotFoundException_WhenCustomerMeasureDoesNotExist() {

            this.mockRepositoryFindCustomerMeasureById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerMeasureService.singleDeleteCustomerMeasure(UUID.randomUUID())
            );
        }
    }
}
