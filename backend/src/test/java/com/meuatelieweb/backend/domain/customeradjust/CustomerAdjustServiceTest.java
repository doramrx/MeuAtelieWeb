package com.meuatelieweb.backend.domain.customeradjust;

import com.meuatelieweb.backend.domain.adjust.Adjust;
import com.meuatelieweb.backend.domain.adjust.AdjustService;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;
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

import static com.meuatelieweb.backend.util.AdjustCreator.createValidAdjust;
import static com.meuatelieweb.backend.util.CustomerAdjustCreator.createValidCustomerAdjust;
import static com.meuatelieweb.backend.util.CustomerAdjustCreator.createValidSaveCustomerAdjustDTO;
import static com.meuatelieweb.backend.domain.orderitem.OrderItemCreator.createValidAdjustOrderItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Adjust Service")
class CustomerAdjustServiceTest {

    @InjectMocks
    private CustomerAdjustService customerAdjustService;

    @Mock
    private CustomerAdjustRepository customerAdjustRepositoryMock;

    @Mock
    private AdjustService adjustServiceMock;


    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(CustomerAdjust customerAdjust) {
            BDDMockito.when(customerAdjustRepositoryMock.findCustomerAdjustById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customerAdjust));
        }

        @Test
        @DisplayName("findById returns customer adjust when successful")
        void findById_ReturnsCustomerAdjust_WhenSuccessful() {
            CustomerAdjust customerAdjust = createValidCustomerAdjust();

            this.mockRepositoryFindById(customerAdjust);

            CustomerAdjust customerAdjustFound = customerAdjustService.findById(UUID.randomUUID());

            assertNotNull(customerAdjustFound);
            assertEquals(customerAdjust.getId(), customerAdjustFound.getId());
            assertEquals(customerAdjust.getAdjustmentCost(), customerAdjustFound.getAdjustmentCost());
            assertEquals(customerAdjust.getAdjust(), customerAdjustFound.getAdjust());
            assertEquals(customerAdjust.getOrderItem(), customerAdjustFound.getOrderItem());
            assertEquals(customerAdjust.getIsActive(), customerAdjustFound.getIsActive());
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when customer adjust is not found")
        void findById_ThrowsEntityNotFoundException_WhenCustomerAdjustIsNotFound() {
            this.mockRepositoryFindById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerAdjustService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws IllegalArgumentException when id customer adjust is null")
        void findById_ThrowsIllegalArgumentException_WhenIdCustomerIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerAdjustService.findById(null));
        }
    }

    @DisplayName("Test addCustomerAdjusts method")
    @Nested
    class AddCustomerAdjustsTests {

        private void mockAdjustServiceGetAdjusts(Set<Adjust> adjusts) {
            BDDMockito.when(adjustServiceMock.getAdjusts(any(Set.class)))
                    .thenReturn(adjusts);
        }

        @Test
        @DisplayName("addCustomerAdjusts returns list of customer adjusts when successful")
        void addCustomerAdjusts_ReturnsListOfCustomerAdjusts_WhenSuccessful() {

            UUID adjustId = UUID.fromString("322730d7-e8c7-4c60-afa4-e6bb875573f8");

            List<SaveCustomerAdjustDTO> saveCustomerAdjustList = List.of(createValidSaveCustomerAdjustDTO());
            saveCustomerAdjustList.get(0).setAdjustmentId(adjustId);

            Set<Adjust> adjusts = Set.of(createValidAdjust());
            adjusts.iterator().next().setId(adjustId);

            this.mockAdjustServiceGetAdjusts(adjusts);

            ArgumentCaptor<List<CustomerAdjust>> argumentCaptor = ArgumentCaptor.forClass(List.class);

            customerAdjustService.addCustomerAdjusts(createValidAdjustOrderItem(), saveCustomerAdjustList);
            verify(customerAdjustRepositoryMock, atMostOnce()).saveAllAndFlush(argumentCaptor.capture());

            List<CustomerAdjust> customerAdjustsSaved = argumentCaptor.getValue();
            CustomerAdjust customerAdjust = customerAdjustsSaved.get(0);

            assertNotNull(customerAdjust);
            assertEquals(saveCustomerAdjustList.get(0).getAdjustmentId(), customerAdjust.getAdjust().getId());
            assertEquals(adjusts.iterator().next().getName(), customerAdjust.getAdjust().getName());
            assertEquals(adjusts.iterator().next().getCost(), customerAdjust.getAdjust().getCost());
            assertTrue(customerAdjust.getIsActive());
        }

        @Test
        @DisplayName("addCustomerAdjusts throws IllegalArgumentException when customer adjust is null")
        void addCustomerAdjusts_ThrowsIllegalArgumentException_WhenCustomerAdjustIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerAdjustService.addCustomerAdjusts(createValidAdjustOrderItem(), null));
        }

        @Test
        @DisplayName("addCustomerAdjusts throws IllegalArgumentException when item is null")
        void addCustomerAdjusts_ThrowsIllegalArgumentException_WhenItemIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> customerAdjustService.addCustomerAdjusts(
                            null, List.of(createValidSaveCustomerAdjustDTO())
                    )
            );
        }
    }

    @DisplayName("Test deleteCustomerAdjusts method")
    @Nested
    class DeleteCustomerAdjustsTests {

        private void mockRepositoryExistsByIdIn(Boolean exists, Set<UUID> uuidSet) {
            BDDMockito.when(customerAdjustRepositoryMock.existsByIdIn(uuidSet))
                    .thenReturn(exists);
        }

        @Test
        @DisplayName("deleteCustomerAdjusts deletes customer adjusts when successful")
        void deleteCustomerAdjusts_DeletesCustomerAdjusts_WhenSuccessful() {

            Set<UUID> uuidSet = new HashSet<>();
            uuidSet.add(UUID.randomUUID());

            this.mockRepositoryExistsByIdIn(true, uuidSet);

            assertDoesNotThrow(() ->
                    customerAdjustService.deleteCustomerAdjusts(uuidSet)
            );
        }

        @Test
        @DisplayName("deleteCustomerAdjusts throws EntityNotFoundException when customer adjusts do not exist")
        void deleteCustomerAdjusts_ThrowsEntityNotFoundException_WhenCustomerAdjustsDoNotExist() {
            Set<UUID> uuidSet = new HashSet<>();
            uuidSet.add(UUID.randomUUID());

            this.mockRepositoryExistsByIdIn(false, uuidSet);

            assertThrows(EntityNotFoundException.class,
                    () -> customerAdjustService.deleteCustomerAdjusts(uuidSet)
            );
        }

        @Test
        @DisplayName("deleteCustomerAdjusts throws IllegalArgumentException when customer adjusts id is null")
        void deleteCustomerAdjusts_ThrowsIllegalArgumentException_WhenCustomerAdjustsIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> customerAdjustService.deleteCustomerAdjusts(null));
        }
    }

    @DisplayName("Test singleDeleteCustomerAdjust method")
    @Nested
    class SingleDeleteCustomerAdjustTests {

        private void mockRepositoryFindCustomerAdjustById(CustomerAdjust customerAdjustSpy) {
            BDDMockito.when(customerAdjustRepositoryMock.findCustomerAdjustById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customerAdjustSpy));
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjust deletes customer adjust when successful")
        void singleDeleteCustomerAdjust_DeletesCustomerAdjust_WhenSuccessful() {

            CustomerAdjust customerAdjustSpy = spy(createValidCustomerAdjust());
            this.mockRepositoryFindCustomerAdjustById(customerAdjustSpy);

            assertDoesNotThrow(() ->
                    customerAdjustService.singleDeleteCustomerAdjust(customerAdjustSpy.getId())
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjust throws EntityNotFoundException when customer adjust does not exist")
        void singleDeleteCustomerAdjust_ThrowsEntityNotFoundException_WhenCustomerAdjustDoesNotExist() {

            this.mockRepositoryFindCustomerAdjustById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> customerAdjustService.singleDeleteCustomerAdjust(UUID.randomUUID())
            );
        }
    }
}
