package com.meuatelieweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.CustomerController;
import com.meuatelieweb.backend.domain.customer.CustomerService;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
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

            BDDMockito.when(customerServiceMock.findById(Mockito.any(UUID.class)))
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
    }

    @DisplayName("Test addCustomer method")
    @Nested
    class AddCustomerTests {

        @Test
        @DisplayName("addCustomer returns STATUS CODE 201 and customer when successful")
        void addCustomer_ReturnsStatusCode201AndCustomer_WhenSuccessful() throws Exception {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            CustomerDTO customerDTO = createValidCustomerDTO(UUID.randomUUID());

            BDDMockito.when(customerServiceMock.addCustomer(Mockito.any(SaveCustomerDTO.class)))
                    .thenReturn(customerDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveCustomerDTO))
            );

            String expectedLocation = String.format("http://localhost/customers/%s", customerDTO.getId());

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isCreated(),
                            MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(String.valueOf(customerDTO.getId())), UUID.class),
                            MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(customerDTO.getName()), String.class),
                            MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(customerDTO.getEmail()), String.class),
                            MockMvcResultMatchers.jsonPath("$.phone", CoreMatchers.is(customerDTO.getPhone()), String.class),
                            MockMvcResultMatchers.jsonPath("$.isActive", CoreMatchers.is(String.valueOf(customerDTO.getIsActive())), String.class),

                            result -> {
                                String redirectedUrl = result.getResponse().getRedirectedUrl();
                                assertNotNull(redirectedUrl);
                                assertEquals(expectedLocation, redirectedUrl);
                                assertEquals(expectedLocation, result.getResponse().getHeader("Location"));
                            }
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addCustomer returns STATUS CODE 400 when customer is null")
        void addCustomer_ReturnsStatusCode400_WhenCustomerIsNull() throws Exception {

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(null))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addCustomer returns STATUS CODE 400 when customer name is null")
        void addCustomer_ReturnsStatusCode400_WhenCustomerNameIsNull() throws Exception {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            saveCustomerDTO.setName(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveCustomerDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("name")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given name cannot be empty"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addCustomer returns STATUS CODE 400 when customer email is null")
        void addCustomer_ReturnsStatusCode400_WhenCustomerEmailIsNull() throws Exception {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            saveCustomerDTO.setEmail(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveCustomerDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("email")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given email cannot be empty"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addCustomer returns STATUS CODE 400 when customer email is not valid")
        void addCustomer_ReturnsStatusCode400_WhenCustomerEmailIsNotValid() throws Exception {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            saveCustomerDTO.setEmail("abc123");

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveCustomerDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("email")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given email is not valid"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addCustomer returns STATUS CODE 400 when customer phone is not valid")
        void addCustomer_ReturnsStatusCode400_WhenCustomerPhoneIsNotValid() throws Exception {
            SaveCustomerDTO saveCustomerDTO = createValidSaveCustomerDTO();
            saveCustomerDTO.setPhone("000110i");

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveCustomerDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("phone")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given phone is not valid"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @DisplayName("Test updateCustomer method")
    @Nested
    class UpdateCustomerTests {

        @Test
        @DisplayName("updateCustomer returns STATUS CODE 200 and updates customer when successful")
        void updateCustomer_ReturnsStatusCode200AndUpdatesCustomer_WhenSuccessful() throws Exception {
            UpdateCustomerDTO updateCustomerDTO = createValidUpdateCustomerDTO();
            CustomerDTO customerDTO = createValidCustomerDTO(UUID.randomUUID());

            BDDMockito.when(customerServiceMock.updateCustomer(Mockito.any(UUID.class), Mockito.any(UpdateCustomerDTO.class)))
                    .thenReturn(customerDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/customers/{id}", customerDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateCustomerDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(String.valueOf(customerDTO.getId()))),
                            MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(updateCustomerDTO.getName())),
                            MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(customerDTO.getEmail())),
                            MockMvcResultMatchers.jsonPath("$.phone", CoreMatchers.is(updateCustomerDTO.getPhone())),
                            MockMvcResultMatchers.jsonPath("$.isActive", CoreMatchers.is(customerDTO.getIsActive()))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("updateCustomer returns STATUS CODE 400 when customer is null")
        void updateCustomer_ReturnsStatusCode400_WhenCustomerIsNull() throws Exception {
            CustomerDTO customerDTO = createValidCustomerDTO(UUID.randomUUID());

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/customers/{id}", customerDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(null))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("updateCustomer returns STATUS CODE 404 when customer is not found")
        void updateCustomer_ReturnsStatusCode404_WhenCustomerIsNotFound() throws Exception {

            BDDMockito.when(customerServiceMock.updateCustomer(Mockito.any(UUID.class), Mockito.any(UpdateCustomerDTO.class)))
                    .thenThrow(EntityNotFoundException.class);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/customers/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createValidUpdateCustomerDTO()))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isNotFound(),
                            MockMvcResultMatchers.content().string(org.hamcrest.Matchers.emptyOrNullString())
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("updateCustomer returns STATUS CODE 400 when customer name is null")
        void updateCustomer_ReturnsStatusCode400_WhenCustomerNameIsNull() throws Exception {
            UpdateCustomerDTO updateCustomerDTO = createValidUpdateCustomerDTO();
            updateCustomerDTO.setName(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/customers/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateCustomerDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("name")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given name cannot be empty"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("updateCustomer returns STATUS CODE 400 when customer phone is not valid")
        void updateCustomer_ReturnsStatusCode400_WhenCustomerPhoneIsNotValid() throws Exception {
            UpdateCustomerDTO updateCustomerDTO = createValidUpdateCustomerDTO();
            updateCustomerDTO.setPhone("000110i");

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/customers/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateCustomerDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("phone")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given phone is not valid"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @DisplayName("Test deleteCustomer method")
    @Nested
    class DeleteCustomerTests {

        @Test
        @DisplayName("deleteCustomer returns STATUS CODE 204 and inactivates customer when successful")
        void deleteCustomer_ReturnsStatusCode204AndInactivatesCustomer_WhenSuccessful() throws Exception {

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/customers/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isNoContent())
                    .andDo(MockMvcResultHandlers.print());
        }
    }
}
