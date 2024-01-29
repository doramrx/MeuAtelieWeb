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
import jakarta.persistence.EntityNotFoundException;
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
import java.util.UUID;

import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomer;
import static com.meuatelieweb.backend.util.CustomerCreator.createValidCustomerDTO;
import static com.meuatelieweb.backend.util.OrderCreator.createValidOrder;
import static com.meuatelieweb.backend.util.OrderCreator.createValidOrderDTO;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidOrderItem;
import static com.meuatelieweb.backend.util.OrderItemCreator.createValidOrderItemDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        @Test
        @DisplayName("findById returns STATUS CODE 200 and a order when successful")
        void findById_ReturnsStatusCode200AndOrder_WhenSuccessful() throws Exception {

            Order order = createValidOrder();
            Customer customer = createValidCustomer();
            OrderDTO orderDTO = createValidOrderDTO(order.getId(), customer.getId());
            CustomerDTO customerDTO = createValidCustomerDTO(customer.getId());
            OrderItem item = createValidOrderItem();
            OrderItemDTO itemDTO = createValidOrderItemDTO(item.getId());

            BDDMockito.when(orderServiceMock.findById(Mockito.any(UUID.class)))
                    .thenReturn(order);
            mockConverterToOrderDTO(order, orderDTO);
            mockCustomerConverterToCustomerDTO(customer, customerDTO);
            mockOrderItemConverterToOrderItemDTO(item, itemDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/orders/{id}", order.getId())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            result -> {
                                OrderDTO orderDTOFound = objectMapper.readValue(
                                        result.getResponse().getContentAsString(),
                                        OrderDTO.class
                                );

                                assertEquals(orderDTO.getId(), orderDTOFound.getId());
                                assertEquals(orderDTO.getOrderNumber(), orderDTOFound.getOrderNumber());
                                assertEquals(orderDTO.getCreatedAt(), orderDTOFound.getCreatedAt());
                                assertEquals(orderDTO.getUpdatedAt(), orderDTOFound.getUpdatedAt());
                                assertEquals(orderDTO.getFinishedAt(), orderDTOFound.getFinishedAt());
                                assertEquals(orderDTO.getIsActive(), orderDTOFound.getIsActive());
                                assertEquals(orderDTO.getCustomer(), orderDTOFound.getCustomer());
                                assertEquals(orderDTO.getOrderItems(), orderDTOFound.getOrderItems());
                            }
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("findById returns STATUS CODE 404 when order is not found")
        void findById_ReturnsStatusCode404_WhenOrderIsNotFound() throws Exception {

            BDDMockito.when(orderServiceMock.findById(Mockito.any(UUID.class)))
                    .thenThrow(new EntityNotFoundException("The given order does not exist"));

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/orders/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isNotFound(),
                            MockMvcResultMatchers.content().string(org.hamcrest.Matchers.emptyOrNullString())
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }
}
