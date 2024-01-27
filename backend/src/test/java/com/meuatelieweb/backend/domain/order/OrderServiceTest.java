package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.orderitem.OrderItemService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.meuatelieweb.backend.util.OrderCreator.createValidOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

        private void mockRepositoryFindByIdAndIsActiveTrue(Order order) {
            BDDMockito.when(orderRepositoryMock.findByIdAndIsActiveTrue(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(order));
        }

        @Test
        @DisplayName("findByIdAndIsActiveTrue returns order when successful")
        void findByIdAndIsActiveTrue_ReturnsOrder_WhenSuccessful() {
            Order order = createValidOrder();

            this.mockRepositoryFindByIdAndIsActiveTrue(order);

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
            this.mockRepositoryFindByIdAndIsActiveTrue(null);

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
}
