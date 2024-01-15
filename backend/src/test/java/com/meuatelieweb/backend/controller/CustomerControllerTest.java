package com.meuatelieweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.CustomerController;
import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
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

import java.util.List;
import java.util.UUID;

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


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

    @DisplayName("Test findById method")
    @Nested
    class FindByIdTests {

        @Test
        @DisplayName("findById returns STATUS CODE 200 and a customer when successful")
        void findById_ReturnsStatusCode200AndCustomer_WhenSuccessful() throws Exception {

            CustomerDTO customerDTO = createValidCustomerDTO(UUID.randomUUID());

            BDDMockito.when(customerServiceMock.findById(Mockito.any(UUID.class)))
                    .thenReturn(customerDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/customers/{id}", customerDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            result -> {
                                CustomerDTO customerDTOFound = objectMapper.readValue(
                                        result.getResponse().getContentAsString(),
                                        CustomerDTO.class
                                );

                                assertEquals(customerDTO.getId(), customerDTOFound.getId());
                                assertEquals(customerDTO.getName(), customerDTOFound.getName());
                                assertEquals(customerDTO.getEmail(), customerDTOFound.getEmail());
                                assertEquals(customerDTO.getPhone(), customerDTOFound.getPhone());
                                assertEquals(customerDTO.getIsActive(), customerDTOFound.getIsActive());
                            }
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("findById returns STATUS CODE 404 when customer is not found")
        void findById_ReturnsStatusCode404_WhenCustomerIsNotFound() throws Exception {

            BDDMockito.when(customerServiceMock.findById(any(UUID.class)))
                    .thenThrow(new EntityNotFoundException("The given user does not exist"));

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/customers/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isNotFound(),
                            MockMvcResultMatchers.content().string(org.hamcrest.Matchers.emptyOrNullString())
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("findById returns STATUS CODE 400 when id customer is null")
        void findById_ReturnsStatusCode400_WhenIdCustomerIsNull() throws Exception {

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/customers/null")
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.details", CoreMatchers.is("Invalid UUID string: null"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }


}
