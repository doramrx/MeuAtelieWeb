package com.meuatelieweb.backend.domain.orderitem;

import com.meuatelieweb.backend.domain.adjust.Adjust;
import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjust;
import com.meuatelieweb.backend.domain.customeradjust.CustomerAdjustService;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustDTO;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustListDTO;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasure;
import com.meuatelieweb.backend.domain.customermeasure.CustomerMeasureService;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.measure.Measure;
import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.orderitem.dto.SaveOrderItemDTO;
import com.meuatelieweb.backend.domain.orderitem.dto.UpdateOrderItemDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static com.meuatelieweb.backend.util.OrderItemCreator.*;
import static com.meuatelieweb.backend.util.AdjustCreator.createValidAdjust;
import static com.meuatelieweb.backend.util.CustomerAdjustCreator.*;
import static com.meuatelieweb.backend.util.CustomerMeasureCreator.*;
import static com.meuatelieweb.backend.util.MeasureCreator.createValidMeasure;
import static com.meuatelieweb.backend.util.OrderCreator.createValidOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for OrderItem Service")
class OrderItemServiceTest {

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepositoryMock;

    @Mock
    private CustomerMeasureService customerMeasureServiceMock;

    @Mock
    private CustomerAdjustService customerAdjustServiceMock;

    private void mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(Boolean exists) {
        BDDMockito.when(orderItemRepositoryMock.existsByIdAndDeliveredAtNull(any(UUID.class)))
                .thenReturn(exists);
    }

    private void mockOrderItemRepositoryFindByIdAndIsActiveTrue(OrderItem orderItem) {
        BDDMockito.when(orderItemRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                .thenReturn(Optional.ofNullable(orderItem));
    }

    private void mockOrderItemRepositoryExistsByIdAndIsActiveTrue(Boolean expectedReturn) {
        BDDMockito.when(orderItemRepositoryMock.existsByIdAndIsActiveTrue(any(UUID.class)))
                .thenReturn(expectedReturn);
    }

    @DisplayName("Test findActiveOrderItemById method")
    @Nested
    class FindActiveOrderItemByIdTests {

        @Test
        @DisplayName("findByIdAndIsActiveTrue returns order item when successful")
        void findByIdAndIsActiveTrue_ReturnsOrderItem_WhenSuccessful() {
            OrderItem orderItem = createValidTailoredOrderItem();
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(orderItem);

            OrderItem orderItemFound = orderItemService.findActiveOrderItemById(UUID.randomUUID());

            assertNotNull(orderItemFound);
            assertEquals(orderItem.getId(), orderItemFound.getId());
            assertEquals(orderItem.getCost(), orderItemFound.getCost());
            assertEquals(orderItem.getTitle(), orderItemFound.getTitle());
            assertEquals(orderItem.getDescription(), orderItemFound.getDescription());
            assertEquals(orderItem.getCreatedAt(), orderItemFound.getCreatedAt());
            assertEquals(orderItem.getDueDate(), orderItemFound.getDueDate());
            assertEquals(orderItem.getDeliveredAt(), orderItemFound.getDeliveredAt());
            assertEquals(orderItem.getIsActive(), orderItemFound.getIsActive());
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue throws EntityNotFoundException when order item is not found")
        void findByIdAndIsActiveTrue_ThrowsEntityNotFoundException_WhenOrderItemIsNotFound() {
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> orderItemService.findActiveOrderItemById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue throws IllegalArgumentException when order item id is not found")
        void findByIdAndIsActiveTrue_ThrowsIllegalArgumentException_WhenOrderItemIdIsNotFound() {
            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.findActiveOrderItemById(null));
        }
    }

    @DisplayName("Test addOrderItems method")
    @Nested
    class AddOrderItemsTests {

        @Captor
        private ArgumentCaptor<List<OrderItem>> orderItemsCaptor;

        private void mockCustomerMeasureServiceAddCustomerMeasures(List<CustomerMeasure> customerMeasures) {
            BDDMockito.when(customerMeasureServiceMock.addCustomerMeasures(any(OrderItem.class), anyList()))
                    .thenReturn(customerMeasures);

        }

        private void mockCustomerAdjustServiceAddCustomerAdjust(List<CustomerAdjust> customerAdjusts) {
            BDDMockito.when(customerAdjustServiceMock.addCustomerAdjusts(any(OrderItem.class), anyList()))
                    .thenReturn(customerAdjusts);

        }

        private void mockRepositorySaveAndFlush(OrderItem item) {
            BDDMockito.when(orderItemRepositoryMock.saveAndFlush(any(OrderItem.class)))
                    .thenReturn(item);
        }

        @Test
        @DisplayName("addOrderItems returns list of order items when successful. Tailored type test")
        void addOrderItems_ReturnsListOfOrderItems_WhenSuccessful_TailoredType() {

            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOTailored());

            this.mockRepositorySaveAndFlush(createValidTailoredOrderItem());
            this.mockCustomerMeasureServiceAddCustomerMeasures(List.of(createValidCustomerMeasure()));

            orderItemService.addOrderItems(createValidOrder(), saveItemsDTO);
            verify(orderItemRepositoryMock, atMostOnce()).saveAllAndFlush(this.orderItemsCaptor.capture());

            List<OrderItem> savedItems = this.orderItemsCaptor.getValue();
            OrderItem tailoredItem = savedItems.get(0);

            SaveOrderItemDTO saveTailoredItemDTO = saveItemsDTO.iterator().next();

            assertNotNull(savedItems);
            assertEquals(tailoredItem.getTitle(), saveTailoredItemDTO.getTitle());
            assertEquals(tailoredItem.getDescription(), saveTailoredItemDTO.getDescription());
            assertEquals(tailoredItem.getCost(), saveTailoredItemDTO.getCost());
            assertEquals(tailoredItem.getDueDate().toLocalDate(), saveTailoredItemDTO.getDueDate().toLocalDate());
            assertNotNull(tailoredItem.getCreatedAt());
            assertNotNull(((TailoredOrderItem) tailoredItem).getCustomerMeasures());
            assertTrue(tailoredItem.getIsActive());
        }

        @Test
        @DisplayName("addOrderItems returns list of order items when successful. Adjust type test")
        void addOrderItems_ReturnsListOfOrderItems_WhenSuccessful_AdjustType() {
            Order order = createValidOrder();

            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOAdjust());

            this.mockRepositorySaveAndFlush(createValidAdjustOrderItem());
            this.mockCustomerAdjustServiceAddCustomerAdjust(List.of(createValidCustomerAdjust()));

            orderItemService.addOrderItems(order, saveItemsDTO);
            verify(orderItemRepositoryMock, atMostOnce()).saveAllAndFlush(this.orderItemsCaptor.capture());

            List<OrderItem> savedItems = this.orderItemsCaptor.getValue();
            OrderItem adjustItem = savedItems.get(0);

            SaveOrderItemDTO saveAdjustItemDTO = saveItemsDTO.iterator().next();

            assertNotNull(savedItems);
            assertEquals(adjustItem.getTitle(), saveAdjustItemDTO.getTitle());
            assertEquals(adjustItem.getDescription(), saveAdjustItemDTO.getDescription());
            assertEquals(adjustItem.getDueDate().toLocalDate(), saveAdjustItemDTO.getDueDate().toLocalDate());
            assertNotNull(adjustItem.getCreatedAt());
            assertNotNull(((AdjustOrderItem) adjustItem).getCustomerAdjustments());
            assertTrue(adjustItem.getIsActive());
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when items list is empty.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenItemsListIsEmpty() {
            Set<SaveOrderItemDTO> emptySaveItemsDTO = Collections.emptySet();

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), emptySaveItemsDTO));
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when item type is null.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenItemTypeIsNull() {
            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOTailored());
            saveItemsDTO.iterator().next().setType(null);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when items have invalid title.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenItemsHaveInvalidTitle() {
            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOTailored());
            saveItemsDTO.iterator().next().setTitle(null);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when items due date is null.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenItemsDueDateIsNull() {
            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOTailored());
            saveItemsDTO.iterator().next().setDueDate(null);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when items due date is invalid.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenItemsDueDateIsInvalid() {
            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOTailored());
            saveItemsDTO.iterator().next().setDueDate(LocalDateTime.now().minusDays(10));

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when adjust order item has duplicated adjusts.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenAdjustOrderItemHasDuplicatedAdjusts() {
            UUID id = UUID.fromString("bd7e9753-9111-4391-9e91-67ff988060cc");

            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOAdjust());
            List<SaveCustomerAdjustDTO> saveCustomerAdjustDTOS = List.of(
                    createValidSaveCustomerAdjustDTO(), createValidSaveCustomerAdjustDTO()
            );

            saveCustomerAdjustDTOS.get(0).setAdjustmentId(id);
            saveCustomerAdjustDTOS.get(1).setAdjustmentId(id);

            saveItemsDTO.iterator().next().setAdjusts(saveCustomerAdjustDTOS);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when tailored order item cost is null.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenTailoredOrderItemCostIsNull() {
            String expectedErrorMessage = "The given cost cannot be empty";

            SaveOrderItemDTO saveOrderItemDTO = createValidSaveOrderItemDTOTailored();
            saveOrderItemDTO.setCost(null);
            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(saveOrderItemDTO);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when tailored order item cost is lesser than 0.01.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenTailoredOrderItemCostIsLesserThan1Cent() {
            String expectedErrorMessage = "The given cost cannot be lesser than 0.01";

            SaveOrderItemDTO saveOrderItemDTO = createValidSaveOrderItemDTOTailored();
            saveOrderItemDTO.setCost(0.001);
            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(saveOrderItemDTO);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when tailored order item has duplicated measures.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenTailoredOrderItemHasDuplicatedMeasures() {
            UUID id = UUID.fromString("bd7e9753-9111-4391-9e91-67ff988060cc");

            Set<SaveOrderItemDTO> saveItemsDTO = Set.of(createValidSaveOrderItemDTOTailored());
            List<SaveCustomerMeasureDTO> saveCustomerMeasuresDTOS = List.of(
                    createValidSaveCustomerMeasureDTO(), createValidSaveCustomerMeasureDTO()
            );

            saveCustomerMeasuresDTOS.get(0).setMeasurementId(id);
            saveCustomerMeasuresDTOS.get(1).setMeasurementId(id);

            saveItemsDTO.iterator().next().setMeasures(saveCustomerMeasuresDTOS);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), saveItemsDTO));
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when order is null.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenOrderIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(null, null)
            );
        }

        @Test
        @DisplayName("addOrderItems throws IllegalArgumentException when items are null.")
        void addOrderItems_ThrowsIllegalArgumentException_WhenItemsAreNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addOrderItems(createValidOrder(), null)
            );
        }

    }

    @DisplayName("Test addAdjustsToOrderItem method")
    @Nested
    class AddAdjustsToOrderItemTests {

        private void mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(Boolean exists) {
            BDDMockito.when(orderItemRepositoryMock.existsByIdAndDeliveredAtNull(any(UUID.class)))
                    .thenReturn(exists);
        }

        private void mockOrderItemRepositoryFindByIdAndIsActiveTrue(OrderItem item) {
            BDDMockito.when(orderItemRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(item));
        }

        @Test
        @DisplayName("addAdjustsToOrderItem adds customer adjustments to item when successful")
        void addAdjustsToOrderItem_AddsCustomerAdjustmentsToItem_WhenSuccessful() {
            OrderItem item = createValidAdjustOrderItem();
            SaveCustomerAdjustListDTO saveCustomerAdjusts = createValidSaveCustomerAdjustListDTO();

            List<CustomerAdjust> customerAdjusts = List.of(createValidCustomerAdjust());

            this.mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);
            this.mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            BDDMockito.when(customerAdjustServiceMock.addCustomerAdjusts(any(OrderItem.class), anyList()))
                    .thenReturn(customerAdjusts);

            orderItemService.addAdjustsToOrderItem(item.getId(), saveCustomerAdjusts);

            verify(orderItemRepositoryMock, atLeastOnce()).findByIdAndIsActiveTrue(any(UUID.class));
            verify(orderItemRepositoryMock, atLeastOnce()).existsByIdAndDeliveredAtNull(any(UUID.class));
            verify(customerAdjustServiceMock, atLeastOnce()).addCustomerAdjusts(any(OrderItem.class), anyList());
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when there is duplicated customer adjusts")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenThereIsDuplicatedCustomerAdjusts() {
            OrderItem item = createValidAdjustOrderItem();

            UUID id = UUID.fromString("b76e7857-5f43-4ab8-a966-a94ab4876613");
            Adjust adjust = createValidAdjust();
            adjust.setId(id);

            SaveCustomerAdjustListDTO saveCustomerAdjusts = createValidSaveCustomerAdjustListDTO();
            saveCustomerAdjusts.getAdjusts().get(0).setAdjustmentId(id);

            CustomerAdjust customerAdjustment = createValidCustomerAdjust();
            customerAdjustment.setAdjust(adjust);

            ((AdjustOrderItem) item).setCustomerAdjustments(List.of(customerAdjustment));

            this.mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);
            this.mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addAdjustsToOrderItem(item.getId(), saveCustomerAdjusts)
            );
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when item is already delivered")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenItemIsAlreadyDelivered() {
            OrderItem item = createValidAdjustOrderItem();
            this.mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(false);

            SaveCustomerAdjustListDTO saveCustomerAdjusts = createValidSaveCustomerAdjustListDTO();

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addAdjustsToOrderItem(item.getId(), saveCustomerAdjusts)
            );
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when item type is TAILORED")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenItemTypeIsTailored() {
            OrderItem item = createValidTailoredOrderItem();

            this.mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);
            this.mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);

            SaveCustomerAdjustListDTO saveCustomerAdjusts = createValidSaveCustomerAdjustListDTO();

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addAdjustsToOrderItem(((TailoredOrderItem) item).getId(), saveCustomerAdjusts)
            );
        }
    }

    @DisplayName("Test addMeasuresToOrderItem method")
    @Nested
    class AddMeasuresToOrderItemTests {

        @Test
        @DisplayName("addMeasuresToOrderItem adds customer measures to item when successful")
        void addMeasuresToOrderItem_AddsCustomerMeasuresToItem_WhenSuccessful() {
            OrderItem item = createValidTailoredOrderItem();
            SaveCustomerMeasureListDTO saveCustomerMeasures = createValidSaveCustomerMeasureListDTO();

            List<CustomerMeasure> customerMeasures = List.of(createValidCustomerMeasure());

            mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            BDDMockito.when(customerMeasureServiceMock.addCustomerMeasures(any(OrderItem.class), anyList()))
                    .thenReturn(customerMeasures);

            orderItemService.addMeasuresToOrderItem(item.getId(), saveCustomerMeasures);

            verify(orderItemRepositoryMock, atLeastOnce()).findByIdAndIsActiveTrue(any(UUID.class));
            verify(orderItemRepositoryMock, atLeastOnce()).existsByIdAndDeliveredAtNull(any(UUID.class));
            verify(customerMeasureServiceMock, atLeastOnce()).addCustomerMeasures(any(OrderItem.class), anyList());
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when there is duplicated customer measures")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenThereIsDuplicatedCustomerMeasures() {
            OrderItem item = createValidTailoredOrderItem();

            UUID id = UUID.fromString("b76e7857-5f43-4ab8-a966-a94ab4876613");
            Measure measure = createValidMeasure();
            measure.setId(id);

            SaveCustomerMeasureListDTO saveCustomerMeasures = createValidSaveCustomerMeasureListDTO();
            saveCustomerMeasures.getMeasures().get(0).setMeasurementId(id);

            CustomerMeasure customerMeasure = createValidCustomerMeasure();
            customerMeasure.setMeasure(measure);

            ((TailoredOrderItem) item).setCustomerMeasures(List.of(customerMeasure));

            mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addMeasuresToOrderItem(item.getId(), saveCustomerMeasures)
            );
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when item is already delivered")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenItemIsAlreadyDelivered() {
            OrderItem item = createValidTailoredOrderItem();
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(false);

            SaveCustomerMeasureListDTO saveCustomerMeasures = createValidSaveCustomerMeasureListDTO();

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addMeasuresToOrderItem(item.getId(), saveCustomerMeasures)
            );
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when item type is ADJUST")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenItemTypeIsAdjust() {
            OrderItem item = createValidAdjustOrderItem();

            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);

            SaveCustomerMeasureListDTO saveCustomerMeasures = createValidSaveCustomerMeasureListDTO();

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.addMeasuresToOrderItem(((AdjustOrderItem) item).getId(), saveCustomerMeasures)
            );
        }
    }

    @DisplayName("Test updateOrderItem method")
    @Nested
    class UpdateOrderItemTests {

        @Test
        @DisplayName("updateOrderItem updates item when successful")
        void updateOrderItem_UpdatesItem_WhenSuccessful() {
            UpdateOrderItemDTO validUpdateOrderItemDTO = createValidUpdateOrderItemDTO();
            validUpdateOrderItemDTO.setDescription("Some order item description");

            OrderItem itemSpy = spy(createValidTailoredOrderItem());

            mockOrderItemRepositoryFindByIdAndIsActiveTrue(itemSpy);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            orderItemService.updateOrderItem(
                    UUID.randomUUID(), validUpdateOrderItemDTO
            );

            verify(itemSpy, atLeastOnce()).setTitle(validUpdateOrderItemDTO.getTitle());
            verify(itemSpy, atLeastOnce()).setDueDate(validUpdateOrderItemDTO.getDueDate());
            verify(itemSpy, atLeastOnce()).setCost(validUpdateOrderItemDTO.getCost());
            verify(itemSpy, atLeastOnce()).setDescription(validUpdateOrderItemDTO.getDescription());
            verify(orderItemRepositoryMock, atMostOnce()).save(any(OrderItem.class));
        }

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when item is already delivered")
        void updateOrderItem_ThrowsIllegalArgumentException_WhenItemIsAlreadyDelivered() {
            UpdateOrderItemDTO validUpdateOrderItemDTO = createValidUpdateOrderItemDTO();
            OrderItem item = createValidTailoredOrderItem();
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(false);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.updateOrderItem(item.getId(), validUpdateOrderItemDTO)
            );
        }

        @Test
        @DisplayName("updateOrderItem throws EntityNotFoundException when item is not found")
        void updateOrderItem_ThrowsEntityNotFoundException_WhenItemIsNotFound() {
            UpdateOrderItemDTO validUpdateOrderItemDTO = createValidUpdateOrderItemDTO();
            OrderItem item = createValidTailoredOrderItem();
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> orderItemService.updateOrderItem(item.getId(), validUpdateOrderItemDTO)
            );
        }

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when item due date is not valid")
        void updateOrderItem_ThrowsEntityNotFoundException_WhenItemDueDateIsNotValid() {
            UpdateOrderItemDTO validUpdateOrderItemDTO = createValidUpdateOrderItemDTO();
            validUpdateOrderItemDTO.setDueDate(LocalDateTime.now().minusDays(10));

            OrderItem item = createValidTailoredOrderItem();

            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.updateOrderItem(item.getId(), validUpdateOrderItemDTO)
            );
        }

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when item cost is not valid")
        void updateOrderItem_ThrowsEntityNotFoundException_WhenItemCostIsNotValid() {
            String expectedErrorMessage = "The given cost cannot be lesser than 0.01";

            UpdateOrderItemDTO validUpdateOrderItemDTO = createValidUpdateOrderItemDTO();
            validUpdateOrderItemDTO.setCost(0.0);

            OrderItem item = createValidTailoredOrderItem();

            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.updateOrderItem(item.getId(), validUpdateOrderItemDTO)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when item cost is null")
        void updateOrderItem_ThrowsEntityNotFoundException_WhenItemCostIsNull() {
            String expectedErrorMessage = "The given cost cannot be empty";

            UpdateOrderItemDTO validUpdateOrderItemDTO = createValidUpdateOrderItemDTO();
            validUpdateOrderItemDTO.setCost(null);

            OrderItem item = createValidTailoredOrderItem();

            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(item);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.updateOrderItem(item.getId(), validUpdateOrderItemDTO)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }
    }

    @DisplayName("Test updateOrderItemCustomerMeasure method")
    @Nested
    class UpdateOrderItemCustomerMeasureTests {

        @Test
        @DisplayName("updateOrderItemCustomerMeasure updates item customer measure when successful")
        void updateOrderItemCustomerMeasure_UpdatesItemCustomerMeasure_WhenSuccessful() {
            UpdateCustomerMeasureDTO validUpdateCustomerMeasure = createValidUpdateCustomerMeasureDTO();

            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(true);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            orderItemService.updateOrderItemCustomerMeasure(
                    UUID.randomUUID(), UUID.randomUUID(), validUpdateCustomerMeasure
            );

            verify(orderItemRepositoryMock, atMostOnce()).existsByIdAndIsActiveTrue(any(UUID.class));
            verify(orderItemRepositoryMock, atMostOnce()).existsByIdAndDeliveredAtNull(any(UUID.class));
            verify(customerMeasureServiceMock, atMostOnce()).updateCustomerMeasure(any(UUID.class), eq(validUpdateCustomerMeasure));
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws EntityNotFoundException when item is not found")
        void updateOrderItemCustomerMeasure_ThrowsEntityNotFoundException_WhenItemIsNotFound() {
            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(false);

            assertThrows(EntityNotFoundException.class,
                    () -> orderItemService.updateOrderItemCustomerMeasure(
                            UUID.randomUUID(), UUID.randomUUID(), createValidUpdateCustomerMeasureDTO()));
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws IllegalArgumentException when item is already delivered")
        void updateOrderItemCustomerMeasure_ThrowsIllegalArgumentException_WhenItemIsAlreadyDelivered() {
            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(true);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(false);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.updateOrderItemCustomerMeasure(
                            UUID.randomUUID(), UUID.randomUUID(), createValidUpdateCustomerMeasureDTO()));
        }

    }

    @DisplayName("Test deliverItem method")
    @Nested
    class DeliverItemTests {

        @Test
        @DisplayName("deliverItem delivers item when successful")
        void deliverItem_DeliversItem_WhenSuccessful() {
            OrderItem itemSpy = spy(createValidTailoredOrderItem());

            mockOrderItemRepositoryFindByIdAndIsActiveTrue(itemSpy);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            orderItemService.deliverItem(UUID.randomUUID());

            verify(orderItemRepositoryMock, atMostOnce()).findByIdAndIsActiveTrue(any(UUID.class));
            verify(orderItemRepositoryMock, atMostOnce()).existsByIdAndDeliveredAtNull(any(UUID.class));
            verify(orderItemRepositoryMock, atMostOnce()).save(itemSpy);
            verify(itemSpy, atMostOnce()).setDeliveredAt(any(LocalDateTime.class));
        }

        @Test
        @DisplayName("deliverItem throws IllegalArgumentException when item was already delivered")
        void deliverItem_ThrowsIllegalArgumentException_WhenItemWasAlreadyDelivered() {
            OrderItem itemSpy = spy(createValidTailoredOrderItem());

            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(false);

            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.deliverItem(UUID.randomUUID()));
        }

        @Test
        @DisplayName("deliverItem throws EntityNotFoundException when item does not exist")
        void deliverItem_ThrowsEntityNotFoundException_WhenItemDoesNotExist() {
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);
            mockOrderItemRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> orderItemService.deliverItem(UUID.randomUUID()));
        }
    }

    @DisplayName("Test deleteOrderItems method")
    @Nested
    class DeleteOrderItemsTests {
        @Captor
        private ArgumentCaptor<Set<UUID>> uuidCaptor;

        private void mockOrderItemRepositoryExistsByIdIn(Boolean exists) {
            BDDMockito.when(orderItemRepositoryMock.existsByIdIn(anySet()))
                    .thenReturn(exists);
        }

        @Test
        @DisplayName("deleteOrderItems deletes customer adjusts and measures and inactivate item when successful")
        void deleteOrderItems_DeletesCustomerAdjustsAndInactivateItem_WhenSuccessful() {

            OrderItem adjustOrderItem = createValidAdjustOrderItem();
            List<CustomerAdjust> customerAdjusts = List.of(
                    createValidCustomerAdjust(),
                    createValidCustomerAdjust()
            );
            ((AdjustOrderItem) adjustOrderItem).setCustomerAdjustments(customerAdjusts);

            List<OrderItem> orderItems = List.of(adjustOrderItem);

            mockOrderItemRepositoryExistsByIdIn(true);

            orderItemService.deleteOrderItems(orderItems);

            verify(customerAdjustServiceMock, times(1)).deleteCustomerAdjusts(this.uuidCaptor.capture());
            Set<UUID> customerAdjustIdsCaptured = this.uuidCaptor.getValue();
            Set<UUID> expectedCustomerAdjustIds = Set.of(customerAdjusts.get(0).getId(), customerAdjusts.get(1).getId());

            assertEquals(2, customerAdjustIdsCaptured.size());
            assertEquals(expectedCustomerAdjustIds, customerAdjustIdsCaptured);
            verify(orderItemRepositoryMock, times(1)).inactivateOrderItemById(anySet());
        }

        @Test
        @DisplayName("deleteOrderItems deletes customer adjusts and measures and inactivate item when successful")
        void deleteOrderItems_DeletesCustomerMeasuresAndInactivateItem_WhenSuccessful() {

            OrderItem tailoredOrderItem = createValidTailoredOrderItem();
            List<CustomerMeasure> customerMeasures = List.of(
                    createValidCustomerMeasure(),
                    createValidCustomerMeasure()
            );
            ((TailoredOrderItem) tailoredOrderItem).setCustomerMeasures(customerMeasures);

            List<OrderItem> orderItems = List.of(tailoredOrderItem);

            mockOrderItemRepositoryExistsByIdIn(true);

            orderItemService.deleteOrderItems(orderItems);

            verify(customerMeasureServiceMock, times(1)).deleteCustomerMeasures(this.uuidCaptor.capture());
            Set<UUID> customerMeasureIdsCaptured = this.uuidCaptor.getValue();
            Set<UUID> expectedCustomerMeasureIds = Set.of(customerMeasures.get(0).getId(), customerMeasures.get(1).getId());

            assertEquals(2, customerMeasureIdsCaptured.size());
            assertEquals(expectedCustomerMeasureIds, customerMeasureIdsCaptured);
            verify(orderItemRepositoryMock, times(1)).inactivateOrderItemById(anySet());
        }

        @Test
        @DisplayName("deleteOrderItems throws EntityNotFoundException when items are not found")
        void deleteOrderItems_ThrowsEntityNotFoundException_WhenItemsAreNotFound() {
            String expectedErrorMessage = "Some of the given order items do not exist";

            this.mockOrderItemRepositoryExistsByIdIn(false);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderItemService.deleteOrderItems(List.of(
                            createValidAdjustOrderItem()))
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        @DisplayName("deleteOrderItems throws IllegalArgumentException when items are null")
        void deleteOrderItems_ThrowsIllegalArgumentException_WhenItemsAreNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.deleteOrderItems(null)
            );
        }

    }

    @DisplayName("Test singleDeleteItemFromOrder method")
    @Nested
    class SingleDeleteItemFromOrderTests {
        @Captor
        private ArgumentCaptor<Set<UUID>> uuidCaptor;

        @Test
        @DisplayName("singleDeleteItemFromOrder throws IllegalArgumentException when item is null")
        void singleDeleteItemFromOrder_ThrowsIllegalArgumentException_WhenItemIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteItemFromOrder(null)
            );
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder throws IllegalArgumentException when item is null")
        void singleDeleteItemFromOrder_ThrowsIllegalArgumentException_WhenItemDeliveredIsNull() {
            String expectedErrorMessage = "The item cannot be modified because it has already been delivered";

            OrderItem orderItem = createValidTailoredOrderItem();
            orderItem.setDeliveredAt(LocalDateTime.now());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteItemFromOrder(orderItem)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder deletes customer adjustments when successful")
        void singleDeleteItemFromOrder_DeletesCustomerAdjustments_WhenSuccessful() {
            OrderItem orderItem = createValidAdjustOrderItem();
            CustomerAdjust someCustomerAdjust = createValidCustomerAdjust();
            CustomerAdjust anotherCustomerAdjust = createValidCustomerAdjust();

            ((AdjustOrderItem) orderItem).setCustomerAdjustments(List.of(
                    someCustomerAdjust,
                    anotherCustomerAdjust
            ));

            orderItemService.singleDeleteItemFromOrder(orderItem);
            verify(customerAdjustServiceMock, times(1)).deleteCustomerAdjusts(uuidCaptor.capture());
            Set<UUID> customerAdjustmentsIds = uuidCaptor.getValue();
            Set<UUID> expectedCustomerAdjustmentIds = Set.of(
                    someCustomerAdjust.getId(),
                    anotherCustomerAdjust.getId()
            );

            assertEquals(2, customerAdjustmentsIds.size());
            assertEquals(expectedCustomerAdjustmentIds, customerAdjustmentsIds);
            verify(orderItemRepositoryMock, times(1)).inactivateOrderItemById(anySet());
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder deletes customer measurements when successful")
        void singleDeleteItemFromOrder_DeletesCustomerMeasurements_WhenSuccessful() {
            OrderItem orderItem = createValidTailoredOrderItem();
            CustomerMeasure someCustomerMeasure = createValidCustomerMeasure();
            CustomerMeasure anotherCustomerMeasure = createValidCustomerMeasure();

            ((TailoredOrderItem) orderItem).setCustomerMeasures(List.of(
                    someCustomerMeasure,
                    anotherCustomerMeasure
            ));

            orderItemService.singleDeleteItemFromOrder(orderItem);
            verify(customerMeasureServiceMock, times(1)).deleteCustomerMeasures(uuidCaptor.capture());
            Set<UUID> customerMeasuresIds = uuidCaptor.getValue();
            Set<UUID> expectedCustomerMeasuresIds = Set.of(
                    someCustomerMeasure.getId(),
                    anotherCustomerMeasure.getId()
            );

            assertEquals(2, customerMeasuresIds.size());
            assertEquals(expectedCustomerMeasuresIds, customerMeasuresIds);
            verify(orderItemRepositoryMock, times(1)).inactivateOrderItemById(anySet());
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder throws IllegalArgumentException when item type is different of type 'TAILORED' or 'ADJUST'")
        void singleDeleteItemFromOrder_ThrowsIllegalArgumentException_WhenTypeDifferentOfTailoredOrAdjust() {
            String expectedErrorMessage = "The item type is invalid";

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteItemFromOrder(createValidOrderItem())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }
    }

    @DisplayName("Test singleDeleteCustomerAdjustFromItem method")
    @Nested
    class SingleDeleteCustomerAdjustFromItemTests {

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws EntityNotFoundException when item does not exist or is inactive")
        void singleDeleteCustomerAdjustFromItem_ThrowsEntityNotFoundException_WhenItemDoesNotExistOrIsInactive() {
            String expectedErrorMessage = "The given item does not exist or is already inactive";

            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(false);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> orderItemService.singleDeleteCustomerAdjustFromItem(
                            UUID.randomUUID(), UUID.randomUUID()
                    )
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
            verifyNoInteractions(customerAdjustServiceMock);
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws IllegalArgumentException when item was delivered")
        void singleDeleteCustomerAdjustFromItem_ThrowsIllegalArgumentException_WhenItemWasDelivered() {
            String expectedErrorMessage = "The item cannot be modified because it has already been delivered";

            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(true);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteCustomerAdjustFromItem(
                            UUID.randomUUID(), UUID.randomUUID()
                    )
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
            verifyNoInteractions(customerAdjustServiceMock);
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem deletes customer adjust from item when successful")
        void singleDeleteCustomerAdjustFromItem_DeletesCustomerAdjust_WhenSuccessful() {

            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(true);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            orderItemService.singleDeleteCustomerAdjustFromItem(
                    UUID.randomUUID(), UUID.randomUUID()
            );

            verify(customerAdjustServiceMock, times(1)).singleDeleteCustomerAdjust(any(UUID.class));
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws IllegalArgumentException when item id is null")
        void singleDeleteCustomerAdjustFromItem_ThrowsIllegalArgumentException_WhenItemIdIsNull() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteCustomerAdjustFromItem(
                            null, null
                    )
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws IllegalArgumentException when item id is null")
        void singleDeleteCustomerAdjustFromItem_ThrowsIllegalArgumentException_WhenCustomerAdjustIdIsNull() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteCustomerAdjustFromItem(
                            UUID.randomUUID(), null
                    )
            );
        }
    }

    @DisplayName("Test singleDeleteCustomerMeasureFromItem method")
    @Nested
    class SingleDeleteCustomerMeasureFromItemTests {

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws EntityNotFoundException when item does not exist or is inactive")
        void singleDeleteCustomerMeasureFromItem_ThrowsEntityNotFoundException_WhenItemDoesNotExistOrIsInactive() {
            String expectedErrorMessage = "The given item does not exist or is already inactive";

            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(false);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> orderItemService.singleDeleteCustomerMeasureFromItem(
                            UUID.randomUUID(), UUID.randomUUID()
                    )
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
            verifyNoInteractions(customerMeasureServiceMock);
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws IllegalArgumentException when item was delivered")
        void singleDeleteCustomerMeasureFromItem_ThrowsIllegalArgumentException_WhenItemWasDelivered() {
            String expectedErrorMessage = "The item cannot be modified because it has already been delivered";

            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(true);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(false);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteCustomerMeasureFromItem(
                            UUID.randomUUID(), UUID.randomUUID()
                    )
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
            verifyNoInteractions(customerMeasureServiceMock);
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem deletes customer measure from item when successful")
        void singleDeleteCustomerMeasureFromItem_DeletesCustomerMeasure_WhenSuccessful() {

            mockOrderItemRepositoryExistsByIdAndIsActiveTrue(true);
            mockOrderItemRepositoryExistsByIdAndDeliveredAtNull(true);

            orderItemService.singleDeleteCustomerMeasureFromItem(
                    UUID.randomUUID(), UUID.randomUUID()
            );

            verify(customerMeasureServiceMock, times(1)).singleDeleteCustomerMeasure(any(UUID.class));
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws IllegalArgumentException when item id is null")
        void singleDeleteCustomerMeasureFromItem_ThrowsIllegalArgumentException_WhenItemIdIsNull() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteCustomerMeasureFromItem(
                            null, null
                    )
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws IllegalArgumentException when item id is null")
        void singleDeleteCustomerMeasureFromItem_ThrowsIllegalArgumentException_WhenCustomerAdjustIdIsNull() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.singleDeleteCustomerMeasureFromItem(
                            UUID.randomUUID(), null
                    )
            );
        }
    }
}
