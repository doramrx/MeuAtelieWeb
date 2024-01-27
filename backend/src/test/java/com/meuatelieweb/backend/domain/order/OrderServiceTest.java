package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.orderitem.OrderItemService;
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

import java.util.List;

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

}
