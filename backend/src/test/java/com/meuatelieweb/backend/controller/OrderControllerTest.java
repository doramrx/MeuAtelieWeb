package com.meuatelieweb.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.OrderController;
import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.CustomerConverter;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.order.Order;
import com.meuatelieweb.backend.domain.order.OrderConverter;
import com.meuatelieweb.backend.domain.order.OrderService;
import com.meuatelieweb.backend.domain.order.dto.OrderDTO;
import com.meuatelieweb.backend.domain.orderitem.OrderItem;
import com.meuatelieweb.backend.domain.orderitem.OrderItemConverter;
import com.meuatelieweb.backend.domain.orderitem.dto.OrderItemDTO;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomerDTO;
import static com.meuatelieweb.backend.util.OrderCreator.createValidOrder;
import static com.meuatelieweb.backend.util.OrderCreator.createValidOrderDTO;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidOrderItemDTO;

@WebMvcTest(controllers = {OrderController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Order Controller")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderController orderController;

    @MockBean
    private OrderService orderServiceMock;

    @MockBean
    private OrderConverter orderConverterMock;

    @MockBean
    private CustomerConverter customerConverterMock;

    @MockBean
    private OrderItemConverter orderItemConverterMock;

    private void mockConverterToOrderDTO(Order order, OrderDTO expectedOrderDTO) {
        BDDMockito.when(orderConverterMock.toOrderDTO(order))
                .thenReturn(expectedOrderDTO);
    }

    private void mockCustomerConverterToCustomerDTO(Customer customer, CustomerDTO expectedCustomerDTO) {
        BDDMockito.when(customerConverterMock.toCustomerDTO(customer))
                .thenReturn(expectedCustomerDTO);
    }

    private void mockOrderItemConverterToOrderItemDTO(OrderItem item, OrderItemDTO expectedItemDTO) {
        BDDMockito.when(orderItemConverterMock.toOrderItemDTO(item))
                .thenReturn(expectedItemDTO);
    }


    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {

        private void mockServiceFindAll(Page<Order> ordersList) {
            BDDMockito.when(orderServiceMock.findAll(
                    Mockito.any(Pageable.class),
                    Mockito.anyInt(),
                    Mockito.any(LocalDateTime.class),
                    Mockito.any(LocalDateTime.class),
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.anyBoolean())
            ).thenReturn(ordersList);
        }

        @Test
        @DisplayName("findAll returns STATUS CODE 200 and a page of orders when successful")
        void findAll_ReturnsStatusCode200AndPageOfOrders_WhenSuccessful() throws Exception {

            List<Order> orders = List.of(createValidOrder());
            Order order = orders.get(0);
            OrderItem item = order.getOrderItems().get(0);

            Page<Order> ordersPage = new PageImpl<>(
                    orders,
                    Pageable.ofSize(20),
                    20);

            this.mockServiceFindAll(ordersPage);
            mockConverterToOrderDTO(
                    order,
                    createValidOrderDTO(order.getId(), order.getCustomer().getId())
            );
            mockCustomerConverterToCustomerDTO(
                    order.getCustomer(),
                    createValidCustomerDTO(order.getCustomer().getId())
            );
            mockOrderItemConverterToOrderItemDTO(
                    item,
                    createValidOrderItemDTO(item.getId())
            );


            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("orderNumber", "1")
                            .queryParam("createdAt", "2024-01-10T10:00:00")
                            .queryParam("finishedAt", "2024-12-10T10:00:00")
                            .queryParam("customerName", "Ada")
                            .queryParam("customerEmail", "ada@lovelace.com")
                            .queryParam("isActive", "true")
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(1))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("findAll returns STATUS CODE 200 and empty page of orders when orders do not exist")
        void findAll_ReturnsStatusCode200AndEmptyPageOfOrders_WhenOrdersDoNotExist() throws Exception {

            Page<Order> ordersPage = new PageImpl<>(
                    List.of(),
                    Pageable.ofSize(20),
                    20);


            this.mockServiceFindAll(ordersPage);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("orderNumber", "1")
                            .queryParam("createdAt", "2024-01-10T10:00:00")
                            .queryParam("finishedAt", "2024-12-10T10:00:00")
                            .queryParam("customerName", "Ada")
                            .queryParam("customerEmail", "ada@lovelace.com")
                            .queryParam("isActive", "true")
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(0))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }
}
