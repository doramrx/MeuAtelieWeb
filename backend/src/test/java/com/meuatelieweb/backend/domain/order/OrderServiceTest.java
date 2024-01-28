package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.customeradjust.dto.SaveCustomerAdjustListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.SaveCustomerMeasureListDTO;
import com.meuatelieweb.backend.domain.customermeasure.dto.UpdateCustomerMeasureDTO;
import com.meuatelieweb.backend.domain.order.dto.SaveOrderDTO;
import com.meuatelieweb.backend.domain.order.dto.UpdateOrderDTO;
import com.meuatelieweb.backend.domain.orderitem.AdjustOrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderItemService;
import com.meuatelieweb.backend.domain.orderitem.TailoredOrderItem;
import com.meuatelieweb.backend.domain.orderitem.dto.UpdateOrderItemDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static com.meuatelieweb.backend.util.CustomerAdjustCreator.createValidSaveCustomerAdjustListDTO;
import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomer;
import static com.meuatelieweb.backend.util.CustomerMeasureCreator.createValidSaveCustomerMeasureListDTO;
import static com.meuatelieweb.backend.util.CustomerMeasureCreator.createValidUpdateCustomerMeasureDTO;
import static com.meuatelieweb.backend.util.OrderCreator.*;
import static com.meuatelieweb.backend.util.OrderItemCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Order Service")
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepositoryMock;

    @Mock
    private CustomerService customerServiceMock;

    @Mock
    private OrderItemService orderItemServiceMock;

    private void mockRepositoryExistsByIdAndFinishedAtNull(Boolean exists) {
        BDDMockito.when(orderRepositoryMock.existsByIdAndFinishedAtNull(any(UUID.class)))
                .thenReturn(exists);
    }

    private void mockRepositoryFindByIdAndIsActiveTrue(Order order) {
        BDDMockito.when(orderRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                .thenReturn(Optional.ofNullable(order));
    }

    private void mockCustomerServiceFindByIdAndIsActiveTrue(Customer customer) {
        BDDMockito.when(customerServiceMock.findByIdAndIsActiveTrue(any(UUID.class)))
                .thenReturn(customer);
    }

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {

        private void mockRepositoryFindAll(PageImpl<Order> pageable) {
            when(orderRepositoryMock.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(pageable);
        }

        @Test
        @DisplayName("findAll returns page of orders when successful")
        void findAll_ReturnsPageOfOrders_WhenSuccessful() {
            List<Order> validOrders = List.of(createValidOrder());
            PageImpl<Order> pageable = new PageImpl<>(validOrders, Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<Order> orderList = orderService.findAll(
                    pageable.getPageable(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ).toList();

            assertNotNull(orderList);
            assertFalse(orderList.isEmpty());
            assertEquals(1, orderList.size());
            assertEquals(validOrders.get(0), orderList.get(0));
        }

        @Test
        @DisplayName("findAll returns an empty page of orders when orders do not exist")
        void findAll_ReturnsEmptyPageOfOrders_WhenOrdersDoNotExist() {
            PageImpl<Order> pageable = new PageImpl<>(List.of(), Pageable.ofSize(20), 20);

            this.mockRepositoryFindAll(pageable);

            List<Order> orderList = orderService.findAll(
                    pageable.getPageable(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ).toList();

            assertNotNull(orderList);
            assertTrue(orderList.isEmpty());
        }
    }

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        private void mockRepositoryFindById(Order order) {
            BDDMockito.when(orderRepositoryMock.findById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(order));
        }

        @Test
        @DisplayName("findById returns order when successful")
        void findById_ReturnsOrder_WhenSuccessful() {
            Order order = createValidOrder();

            this.mockRepositoryFindById(order);

            Order orderFound = orderService.findById(UUID.randomUUID());

            assertNotNull(orderFound);
            assertEquals(order.getId(), orderFound.getId());
            assertEquals(order.getOrderNumber(), orderFound.getOrderNumber());
            assertEquals(order.getCreatedAt(), orderFound.getCreatedAt());
            assertEquals(order.getUpdatedAt(), orderFound.getUpdatedAt());
            assertEquals(order.getFinishedAt(), orderFound.getFinishedAt());
            assertEquals(order.getIsActive(), orderFound.getIsActive());
        }

        @Test
        @DisplayName("findById throws EntityNotFoundException when order is not found")
        void findById_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            this.mockRepositoryFindById(null);

            assertThrows(EntityNotFoundException.class,
                    () -> orderService.findById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findById throws IllegalArgumentException when id order is null")
        void findById_ThrowsIllegalArgumentException_WhenIdOrderIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> orderService.findById(null));
        }
    }

    @DisplayName("Test findByIdAndIsActiveTrue method")
    @Nested
    class FindByIdAndIsActiveTrueTests {

        @Test
        @DisplayName("findByIdAndIsActiveTrue returns order when successful")
        void findByIdAndIsActiveTrue_ReturnsOrder_WhenSuccessful() {
            Order order = createValidOrder();

            mockRepositoryFindByIdAndIsActiveTrue(order);

            Order orderFound = orderService.findByIdAndIsActiveTrue(UUID.randomUUID());

            assertNotNull(orderFound);
            assertEquals(order.getId(), orderFound.getId());
            assertEquals(order.getOrderNumber(), orderFound.getOrderNumber());
            assertEquals(order.getCreatedAt(), orderFound.getCreatedAt());
            assertEquals(order.getUpdatedAt(), orderFound.getUpdatedAt());
            assertEquals(order.getFinishedAt(), orderFound.getFinishedAt());
            assertEquals(order.getIsActive(), orderFound.getIsActive());
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue throws EntityNotFoundException when order is not found")
        void findByIdAndIsActiveTrue_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryFindByIdAndIsActiveTrue(null);

            assertThrows(EntityNotFoundException.class,
                    () -> orderService.findByIdAndIsActiveTrue(UUID.randomUUID()));
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue throws IllegalArgumentException when id order is null")
        void findByIdAndIsActiveTrue_ThrowsIllegalArgumentException_WhenIdOrderIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> orderService.findByIdAndIsActiveTrue(null));
        }
    }

    @DisplayName("Test addOrder method")
    @Nested
    class AddOrderTests {

        private void mockRepositoryGetNextOrderNumber(Integer orderNumber) {
            BDDMockito.when(orderRepositoryMock.getNextOrderNumber())
                    .thenReturn(orderNumber);
        }

        private void mockOrderItemServiceAddOrderItems(List<OrderItem> orderItems) {
            BDDMockito.when(orderItemServiceMock.addOrderItems(any(Order.class), anySet()))
                    .thenReturn(orderItems);
        }

        private void mockRepositorySaveAndFlush(Order order) {
            BDDMockito.when(orderRepositoryMock.saveAndFlush(any(Order.class)))
                    .thenReturn(order);
        }

        @Test
        @DisplayName("addOrder returns order when successful")
        void addOrder_ReturnsOrder_WhenSuccessful() {
            Customer customer = createValidCustomer();
            mockCustomerServiceFindByIdAndIsActiveTrue(customer);

            List<OrderItem> orderItems = List.of(createValidOrderItem());
            this.mockOrderItemServiceAddOrderItems(orderItems);

            Integer orderNumber = 12345;
            this.mockRepositoryGetNextOrderNumber(orderNumber);

            SaveOrderDTO saveOrderDTO = createValidSaveOrderDTO(
                    customer.getId(), Set.of(createValidSaveOrderItemDTOAdjust())
            );

            Order orderSpy = spy(createValidOrder());
            this.mockRepositorySaveAndFlush(orderSpy);

            ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
            orderService.addOrder(saveOrderDTO);
            verify(orderRepositoryMock, times(1)).saveAndFlush(argumentCaptor.capture());

            Order savedOrder = argumentCaptor.getValue();

            assertNotNull(savedOrder);
            assertEquals(customer.getId(), savedOrder.getCustomer().getId());
            assertEquals(orderNumber, savedOrder.getOrderNumber());
            assertNotNull(savedOrder.getCreatedAt());
            assertTrue(savedOrder.getIsActive());
            verify(orderSpy, times(1)).setOrderItems(anyList());
        }

        @Test
        @DisplayName("addOrder throws IllegalArgumentException when order is null")
        void addOrder_ThrowsIllegalArgumentException_WhenOrderIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addOrder(null));
        }
    }

    @DisplayName("Test addOrderItemToOrder method")
    @Nested
    class AddOrderItemToOrderTests {

        private void mockOrderItemServiceAddOrderItems(List<OrderItem> orderItems) {
            BDDMockito.when(orderItemServiceMock.addOrderItems(any(Order.class), anySet()))
                    .thenReturn(orderItems);
        }

        private void mockRepositorySaveAndFlush(Order order) {
            BDDMockito.when(orderRepositoryMock.saveAndFlush(any(Order.class)))
                    .thenReturn(order);
        }

        @Test
        @DisplayName("addOrderItemToOrder throws IllegalArgumentException when order id is null")
        void addOrderItemToOrder_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addOrderItemToOrder(null, createValidSaveOrderItemDTOAdjust()));
        }

        @Test
        @DisplayName("addOrderItemToOrder throws IllegalArgumentException when save order item is null")
        void addOrderItemToOrder_ThrowsIllegalArgumentException_WhenSaveOrderItemIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addOrderItemToOrder(UUID.randomUUID(), null));
        }

        @Test
        @DisplayName("addOrderItemToOrder throws IllegalArgumentException when order was already finished")
        void addOrderItemToOrder_ThrowsIllegalArgumentException_WhenOrderWasAlreadyFinished() {
            String expectedMessage = "The order cannot be modified because it has already been finished";
            mockRepositoryExistsByIdAndFinishedAtNull(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.addOrderItemToOrder(UUID.randomUUID(), createValidSaveOrderItemDTOAdjust()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addOrderItemToOrder throws EntityNotFoundException when order is not active")
        void addOrderItemToOrder_ThrowsEntityNotFoundException_WhenOrderIsNotActive() {
            String expectedMessage = "The given order does not exist or is already inactive";
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.addOrderItemToOrder(UUID.randomUUID(), createValidSaveOrderItemDTOAdjust()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addOrderItemToOrder returns order when successful")
        void addOrderItemToOrder_ReturnsOrder_WhenSuccessful() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);

            Order orderSpy = spy(createValidOrder());
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            List<OrderItem> orderItems = List.of(createValidOrderItem());
            this.mockOrderItemServiceAddOrderItems(orderItems);

            this.mockRepositorySaveAndFlush(orderSpy);

            ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
            orderService.addOrderItemToOrder(orderSpy.getId(), createValidSaveOrderItemDTOAdjust());
            verify(orderRepositoryMock, times(1)).saveAndFlush(argumentCaptor.capture());

            Order savedOrder = argumentCaptor.getValue();

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            assertNotNull(savedOrder.getUpdatedAt());
        }
    }

    @DisplayName("Test addAdjustsToOrderItem method")
    @Nested
    class AddAdjustsToOrderItemTests {

        private void mockOrderItemServiceAddAdjustsToOrderItem(AdjustOrderItem adjustOrderItem) {
            BDDMockito.when(orderItemServiceMock.addAdjustsToOrderItem(
                    any(UUID.class), any(SaveCustomerAdjustListDTO.class))
            ).thenReturn(adjustOrderItem);
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when order id is null")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addAdjustsToOrderItem(
                            null, UUID.randomUUID(), createValidSaveCustomerAdjustListDTO())
            );
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when item order id is null")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenItemOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addAdjustsToOrderItem(
                            UUID.randomUUID(), null, createValidSaveCustomerAdjustListDTO())
            );
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when customer adjusts list is null")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenCustomerAdjustsListIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addAdjustsToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), null)
            );
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when order do not exists or was already finished")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenOrderDoNotExistsOrWasAlreadyFinished() {
            mockRepositoryExistsByIdAndFinishedAtNull(false);
            String expectedMessage = "The order cannot be modified because it has already been finished";

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.addAdjustsToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), createValidSaveCustomerAdjustListDTO())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws EntityNotFoundException when order is not found")
        void addAdjustsToOrderItem_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.addAdjustsToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), createValidSaveCustomerAdjustListDTO())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addAdjustsToOrderItem throws IllegalArgumentException when customer adjusts list is empty")
        void addAdjustsToOrderItem_ThrowsIllegalArgumentException_WhenCustomerAdjustsListIsEmpty() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(createValidOrder());
            String expectedMessage = "There are no adjusts to add to order item";

            SaveCustomerAdjustListDTO adjustOrderItem = createValidSaveCustomerAdjustListDTO();
            adjustOrderItem.setAdjusts(Collections.emptyList());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.addAdjustsToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), adjustOrderItem)
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addAdjustsToOrderItem returns order when successful")
        void addAdjustsToOrderItem_ReturnsOrder_WhenSuccessful() {
            Order orderSpy = spy(createValidOrder());
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            AdjustOrderItem adjustOrderItem = (AdjustOrderItem) createValidAdjustOrderItem();
            this.mockOrderItemServiceAddAdjustsToOrderItem(adjustOrderItem);


            orderService.addAdjustsToOrderItem(
                    UUID.randomUUID(), UUID.randomUUID(), createValidSaveCustomerAdjustListDTO()
            );

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderSpy, times(1)).setOrderItems(anyList());
            verify(orderSpy, times(1)).getOrderItems();
        }
    }

    @DisplayName("Test addMeasuresToOrderItem method")
    @Nested
    class AddMeasuresToOrderItemTests {

        private void mockOrderItemServiceAddMeasuresToOrderItem(TailoredOrderItem tailoredOrderItem) {
            BDDMockito.when(orderItemServiceMock.addMeasuresToOrderItem(
                    any(UUID.class), any(SaveCustomerMeasureListDTO.class))
            ).thenReturn(tailoredOrderItem);
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when order id is null")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addMeasuresToOrderItem(
                            null, UUID.randomUUID(), createValidSaveCustomerMeasureListDTO())
            );
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when item order id is null")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenItemOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addMeasuresToOrderItem(
                            UUID.randomUUID(), null, createValidSaveCustomerMeasureListDTO())
            );
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when customer measures list is null")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenCustomerMeasuresListIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.addMeasuresToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), null)
            );
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when order do not exists or was already finished")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenOrderDoNotExistsOrWasAlreadyFinished() {
            mockRepositoryExistsByIdAndFinishedAtNull(false);
            String expectedMessage = "The order cannot be modified because it has already been finished";

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.addMeasuresToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), createValidSaveCustomerMeasureListDTO())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws EntityNotFoundException when order is not found")
        void addMeasuresToOrderItem_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.addMeasuresToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), createValidSaveCustomerMeasureListDTO())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addMeasuresToOrderItem throws IllegalArgumentException when customer measures list is empty")
        void addMeasuresToOrderItem_ThrowsIllegalArgumentException_WhenCustomerMeasuresListIsEmpty() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(createValidOrder());
            String expectedMessage = "There are no measures to add to order item";

            SaveCustomerMeasureListDTO tailoredOrderItem = createValidSaveCustomerMeasureListDTO();
            tailoredOrderItem.setMeasures(Collections.emptyList());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.addMeasuresToOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), tailoredOrderItem)
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("addMeasuresToOrderItem returns order when successful")
        void addMeasuresToOrderItem_ReturnsOrder_WhenSuccessful() {
            Order orderSpy = spy(createValidOrder());
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            TailoredOrderItem tailoredOrderItem = (TailoredOrderItem) createValidTailoredOrderItem();
            this.mockOrderItemServiceAddMeasuresToOrderItem(tailoredOrderItem);


            orderService.addMeasuresToOrderItem(
                    UUID.randomUUID(), UUID.randomUUID(), createValidSaveCustomerMeasureListDTO()
            );

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderSpy, times(1)).setOrderItems(anyList());
            verify(orderSpy, times(1)).getOrderItems();
        }
    }

    @DisplayName("Test updateOrder method")
    @Nested
    class UpdateOrderTests {

        @Test
        @DisplayName("updateOrder throws IllegalArgumentException when order id is null")
        void updateOrder_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            Customer customer = createValidCustomer();

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrder(
                            null, createValidUpdateOrderDTO(customer.getId()))
            );
        }

        @Test
        @DisplayName("updateOrder throws IllegalArgumentException when update order is null")
        void updateOrder_ThrowsIllegalArgumentException_WhenUpdateOrderIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrder(
                            UUID.randomUUID(), null)
            );
        }

        @Test
        @DisplayName("updateOrder throws IllegalArgumentException when order do not exists or was already finished")
        void updateOrder_ThrowsIllegalArgumentException_WhenOrderDoNotExistsOrWasAlreadyFinished() {
            mockRepositoryExistsByIdAndFinishedAtNull(false);

            Customer customer = createValidCustomer();

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrder(
                            UUID.randomUUID(), createValidUpdateOrderDTO(customer.getId()))
            );
        }

        @Test
        @DisplayName("updateOrder updates order when successful")
        void updateOrder_UpdatesOrder_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            Customer customer = createValidCustomer();
            mockCustomerServiceFindByIdAndIsActiveTrue(customer);

            UpdateOrderDTO updateOrderDTO = createValidUpdateOrderDTO(customer.getId());

            orderService.updateOrder(UUID.randomUUID(), updateOrderDTO);

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setCustomer(any(Customer.class));
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderRepositoryMock, times(1)).save(any(Order.class));
        }
    }

    @DisplayName("Test updateOrderItem method")
    @Nested
    class UpdateOrderItemTests {

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when order id is null")
        void updateOrderItem_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItem(
                            null, UUID.randomUUID(), createValidUpdateOrderItemDTO())
            );
        }

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when order item id is null")
        void updateOrderItem_ThrowsIllegalArgumentException_WhenOrderItemIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItem(
                            UUID.randomUUID(), null, createValidUpdateOrderItemDTO())
            );
        }

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when update order item is null")
        void updateOrderItem_ThrowsIllegalArgumentException_WhenUpdateOrderItemIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), null)
            );
        }

        @Test
        @DisplayName("updateOrderItem throws IllegalArgumentException when order do not exists or was already finished")
        void updateOrderItem_ThrowsIllegalArgumentException_WhenOrderWasAlreadyFinished() {
            String expectedMessage = "The order cannot be modified because it has already been finished";
            mockRepositoryExistsByIdAndFinishedAtNull(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItem(UUID.randomUUID(), UUID.randomUUID(), createValidUpdateOrderItemDTO()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("updateOrderItem throws EntityNotFoundException when order is not found")
        void updateOrderItem_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.updateOrderItem(
                            UUID.randomUUID(), UUID.randomUUID(), createValidUpdateOrderItemDTO())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("updateOrderItem updates order item when successful")
        void updateOrderItem_UpdatesOrderItem_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            UpdateOrderItemDTO updateOrderItemDTO = createValidUpdateOrderItemDTO();

            orderService.updateOrderItem(UUID.randomUUID(), UUID.randomUUID(), updateOrderItemDTO);

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderRepositoryMock, times(1)).save(any(Order.class));
        }
    }

    @DisplayName("Test updateOrderItemCustomerMeasure method")
    @Nested
    class UpdateOrderItemCustomerMeasureTests {

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws IllegalArgumentException when order id is null")
        void updateOrderItemCustomerMeasure_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItemCustomerMeasure(
                            null, UUID.randomUUID(), UUID.randomUUID(), createValidUpdateCustomerMeasureDTO())
            );
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws IllegalArgumentException when order item id is null")
        void updateOrderItemCustomerMeasure_ThrowsIllegalArgumentException_WhenOrderItemIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItemCustomerMeasure(
                            UUID.randomUUID(), null, UUID.randomUUID(), createValidUpdateCustomerMeasureDTO())
            );
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws IllegalArgumentException when customer measure id is null")
        void updateOrderItemCustomerMeasure_ThrowsIllegalArgumentException_WhenCustomerMeasureIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItemCustomerMeasure(
                            UUID.randomUUID(), UUID.randomUUID(), null, createValidUpdateCustomerMeasureDTO())
            );
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws IllegalArgumentException when update customer measure is null")
        void updateOrderItemCustomerMeasure_ThrowsIllegalArgumentException_WhenUpdateCustomerMeasureIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItemCustomerMeasure(
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null)
            );
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws IllegalArgumentException when order do not exists or was already finished")
        void updateOrderItemCustomerMeasure_ThrowsIllegalArgumentException_WhenOrderWasAlreadyFinished() {
            String expectedMessage = "The order cannot be modified because it has already been finished";
            mockRepositoryExistsByIdAndFinishedAtNull(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.updateOrderItemCustomerMeasure(
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), createValidUpdateCustomerMeasureDTO()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure throws EntityNotFoundException when order is not found")
        void updateOrderItemCustomerMeasure_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.updateOrderItemCustomerMeasure(
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), createValidUpdateCustomerMeasureDTO())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("updateOrderItemCustomerMeasure updates customer measure of item when successful")
        void updateOrderItemCustomerMeasure_UpdatesCustomerMeasureOfItem_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            UpdateCustomerMeasureDTO updateCustomerMeasureDTO = createValidUpdateCustomerMeasureDTO();

            orderService.updateOrderItemCustomerMeasure(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), updateCustomerMeasureDTO
            );

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderRepositoryMock, times(1)).saveAndFlush(any(Order.class));
        }
    }

    @DisplayName("Test deliverItem method")
    @Nested
    class DeliverItemTests {

        @Test
        @DisplayName("deliverItem throws IllegalArgumentException when order id is null")
        void deliverItem_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.deliverItem(null, UUID.randomUUID()));
        }

        @Test
        @DisplayName("deliverItem throws IllegalArgumentException when order item id is null")
        void deliverItem_ThrowsIllegalArgumentException_WhenOrderItemIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.deliverItem(UUID.randomUUID(), null));
        }

        @Test
        @DisplayName("deliverItem throws IllegalArgumentException when order do not exists or was already finished")
        void deliverItem_ThrowsIllegalArgumentException_WhenOrderWasAlreadyFinished() {
            String expectedMessage = "The order cannot be modified because it has already been finished";
            mockRepositoryExistsByIdAndFinishedAtNull(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.deliverItem(
                            UUID.randomUUID(), UUID.randomUUID()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("deliverItem throws EntityNotFoundException when order is not found")
        void deliverItem_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.deliverItem(
                            UUID.randomUUID(), UUID.randomUUID()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("deliverItem updates customer measure of item when successful")
        void deliverItem_UpdatesCustomerMeasureOfItem_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            orderService.deliverItem(UUID.randomUUID(), UUID.randomUUID());

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderRepositoryMock, times(1)).save(any(Order.class));
        }
    }

    @DisplayName("Test deleteOrder method")
    @Nested
    class DeleteOrderTests {

        @Test
        @DisplayName("deleteOrder throws IllegalArgumentException when order id is null")
        void deleteOrder_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.deleteOrder(null));
        }

        @Test
        @DisplayName("deleteOrder throws EntityNotFoundException when order is not found")
        void deleteOrder_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.deleteOrder(UUID.randomUUID()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("deleteOrder inactivates order when successful")
        void deleteOrder_InactivatesOrder_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            assertDoesNotThrow(() -> orderService.deleteOrder(UUID.randomUUID()));
        }
    }

    @DisplayName("Test singleDeleteCustomerAdjustFromItem method")
    @Nested
    class SingleDeleteCustomerAdjustFromItemTests {

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws IllegalArgumentException when order id is null")
        void singleDeleteCustomerAdjustFromItem_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerAdjustFromItem(
                            null, UUID.randomUUID(), UUID.randomUUID())
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws IllegalArgumentException when order item id is null")
        void singleDeleteCustomerAdjustFromItem_ThrowsIllegalArgumentException_WhenOrderItemIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerAdjustFromItem(
                            UUID.randomUUID(), null, UUID.randomUUID())
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws IllegalArgumentException when customer adjust id is null")
        void singleDeleteCustomerAdjustFromItem_ThrowsIllegalArgumentException_WhenCustomerAdjustIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerAdjustFromItem(
                            UUID.randomUUID(), UUID.randomUUID(), null)
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws IllegalArgumentException when order do not exists or was already finished")
        void singleDeleteCustomerAdjustFromItem_ThrowsIllegalArgumentException_WhenOrderWasAlreadyFinished() {
            String expectedMessage = "The order cannot be modified because it has already been finished";
            mockRepositoryExistsByIdAndFinishedAtNull(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerAdjustFromItem(
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem throws EntityNotFoundException when order is not found")
        void singleDeleteCustomerAdjustFromItem_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.singleDeleteCustomerAdjustFromItem(
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("singleDeleteCustomerAdjustFromItem deletes customer adjust from item when successful")
        void singleDeleteCustomerAdjustFromItem_DeletesCustomerAdjustFromItem_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            orderService.singleDeleteCustomerAdjustFromItem(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
            );

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderRepositoryMock, times(1)).save(any(Order.class));
        }
    }

    @DisplayName("Test singleDeleteCustomerMeasureFromItem method")
    @Nested
    class SingleDeleteCustomerMeasureFromItemTests {

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws IllegalArgumentException when order id is null")
        void singleDeleteCustomerMeasureFromItem_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerMeasureFromItem(
                            null, UUID.randomUUID(), UUID.randomUUID())
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws IllegalArgumentException when order item id is null")
        void singleDeleteCustomerMeasureFromItem_ThrowsIllegalArgumentException_WhenOrderItemIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerMeasureFromItem(
                            UUID.randomUUID(), null, UUID.randomUUID())
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws IllegalArgumentException when customer adjust id is null")
        void singleDeleteCustomerMeasureFromItem_ThrowsIllegalArgumentException_WhenCustomerAdjustIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerMeasureFromItem(
                            UUID.randomUUID(), UUID.randomUUID(), null)
            );
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws IllegalArgumentException when order do not exists or was already finished")
        void singleDeleteCustomerMeasureFromItem_ThrowsIllegalArgumentException_WhenOrderWasAlreadyFinished() {
            String expectedMessage = "The order cannot be modified because it has already been finished";
            mockRepositoryExistsByIdAndFinishedAtNull(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteCustomerMeasureFromItem(
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem throws EntityNotFoundException when order is not found")
        void singleDeleteCustomerMeasureFromItem_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.singleDeleteCustomerMeasureFromItem(
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
            );

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("singleDeleteCustomerMeasureFromItem deletes customer measure from item when successful")
        void singleDeleteCustomerMeasureFromItem_DeletesCustomerMeasureFromItem_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);

            orderService.singleDeleteCustomerMeasureFromItem(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
            );

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderRepositoryMock, times(1)).save(any(Order.class));
        }
    }

    @DisplayName("Test singleDeleteItemFromOrder method")
    @Nested
    class SingleDeleteItemFromOrderTests {

        private void mockOrderItemServiceFindActiveOrderItemById(OrderItem item) {
            BDDMockito.when(orderItemServiceMock.findActiveOrderItemById(any(UUID.class)))
                    .thenReturn(item);
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder throws IllegalArgumentException when order id is null")
        void singleDeleteItemFromOrder_ThrowsIllegalArgumentException_WhenOrderIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteItemFromOrder(null, UUID.randomUUID()));
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder throws IllegalArgumentException when order item id is null")
        void singleDeleteItemFromOrder_ThrowsIllegalArgumentException_WhenOrderItemIdIsNull() {

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.singleDeleteItemFromOrder(null, UUID.randomUUID()));
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder throws EntityNotFoundException when order is not found")
        void singleDeleteItemFromOrder_ThrowsEntityNotFoundException_WhenOrderIsNotFound() {
            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(null);
            String expectedMessage = "The given order does not exist or is already inactive";

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> orderService.singleDeleteItemFromOrder(UUID.randomUUID(), UUID.randomUUID()));

            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder deletes order item from order when successful")
        void singleDeleteItemFromOrder_DeletesOrderItemFromOrder_WhenSuccessful() {

            Order orderSpy = spy(createValidOrder());
            OrderItem orderItem = createValidOrderItem();

            orderSpy.setOrderItems(List.of(orderItem, createValidOrderItem()));

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);
            this.mockOrderItemServiceFindActiveOrderItemById(orderItem);

            orderService.singleDeleteItemFromOrder(UUID.randomUUID(), UUID.randomUUID());

            assertNotNull(orderSpy.getUpdatedAt());
            verify(orderSpy, times(1)).setUpdatedAt(any(LocalDateTime.class));
            verify(orderRepositoryMock, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("singleDeleteItemFromOrder deletes order and item from order when successful")
        void singleDeleteItemFromOrder_DeletesOrderAndItem_WhenSuccessful() {

            Order order = createValidOrder();
            OrderItem orderItem = createValidOrderItem();
            order.getOrderItems().get(0).setIsActive(false);
            orderItem.setIsActive(false);
            order.setOrderItems(List.of(orderItem));

            Order orderSpy = spy(order);

            mockRepositoryExistsByIdAndFinishedAtNull(true);
            mockRepositoryFindByIdAndIsActiveTrue(orderSpy);
            this.mockOrderItemServiceFindActiveOrderItemById(orderItem);

            orderService.singleDeleteItemFromOrder(UUID.randomUUID(), UUID.randomUUID());

            assertNull(orderSpy.getUpdatedAt());
            verify(orderRepositoryMock, times(1)).inactivateOrderById(any(UUID.class));
            verify(orderItemServiceMock, times(1)).deleteOrderItems(anyList());
        }
    }
}

