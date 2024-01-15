package com.meuatelieweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.CustomerController;
import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
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

import java.util.List;
import java.util.UUID;

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.*;
import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(controllers = {CustomerController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Customer Controller")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private CustomerController customerController;

    @MockBean
    private CustomerService customerServiceMock;

    private static final UUID ID = UUID.fromString("320d780e-ef58-457e-9011-0afa0ef57665");

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {

        private void mockServiceFindAll(Page<CustomerDTO> customersList) {
            BDDMockito.when(customerServiceMock.findAll(
                    Mockito.any(Pageable.class),
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.anyBoolean())
            ).thenReturn(customersList);
        }

        @Test
        @DisplayName("findAll returns STATUS CODE 200 and a page of customers when successful")
        void findAll_ReturnsStatusCode200AndPageOfCustomers_WhenSuccessful() throws Exception {

            List<CustomerDTO> customerDTOList = List.of(
                    createValidCustomerDTO(UUID.randomUUID()),
                    createValidCustomerDTO(UUID.randomUUID())
            );

            Page<CustomerDTO> customersList = new PageImpl<>(
                    customerDTOList,
                    Pageable.ofSize(20),
                    20);

            this.mockServiceFindAll(customersList);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Ada")
                            .queryParam("email", "ada@lovelace.com")
                            .queryParam("phone", "00100110100")
                            .queryParam("isActive", "true")
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(2))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("findAll returns STATUS CODE 200 and empty page of customers when customers do not exist")
        void findAll_ReturnsStatusCode200AndEmptyPageOfCustomers_WhenCustomersDoNotExist() throws Exception {

            Page<CustomerDTO> customersList = new PageImpl<>(
                    List.of(),
                    Pageable.ofSize(20),
                    20);

            this.mockServiceFindAll(customersList);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Ada")
                            .queryParam("email", "ada@lovelace.com")
                            .queryParam("phone", "00100110100")
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
