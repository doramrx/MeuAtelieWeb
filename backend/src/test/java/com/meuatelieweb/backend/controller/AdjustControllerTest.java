package com.meuatelieweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.AdjustController;
import com.meuatelieweb.backend.domain.adjust.AdjustService;
import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
import com.meuatelieweb.backend.domain.adjust.dto.SaveUpdateAdjustDTO;
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

import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.createValidAdjustDTO;
import static com.meuatelieweb.backend.domain.adjust.AdjustCreator.createValidSaveUpdateAdjustDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(controllers = {AdjustController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Adjust Controller")
class AdjustControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private AdjustController adjustController;

    @MockBean
    private AdjustService adjustServiceMock;

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {

        private void mockServiceFindAll(Page<AdjustDTO> adjustsList) {
            BDDMockito.when(adjustServiceMock.findAll(
                    Mockito.any(Pageable.class),
                    Mockito.anyString(),
                    Mockito.anyBoolean())
            ).thenReturn(adjustsList);
        }

        @Test
        @DisplayName("findAll returns STATUS CODE 200 and a page of adjusts when successful")
        void findAll_ReturnsStatusCode200AndPageOfAdjusts_WhenSuccessful() throws Exception {

            List<AdjustDTO> adjustDTOList = List.of(
                    createValidAdjustDTO(UUID.randomUUID()),
                    createValidAdjustDTO(UUID.randomUUID())
            );

            Page<AdjustDTO> adjustsList = new PageImpl<>(
                    adjustDTOList,
                    Pageable.ofSize(20),
                    20);

            this.mockServiceFindAll(adjustsList);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/adjusts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Ajuste")
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
        @DisplayName("findAll returns STATUS CODE 200 and empty page of adjusts when adjusts do not exist")
        void findAll_ReturnsStatusCode200AndEmptyPageOfAdjusts_WhenAdjustsDoNotExist() throws Exception {

            Page<AdjustDTO> adjustsList = new PageImpl<>(
                    List.of(),
                    Pageable.ofSize(20),
                    20);

            this.mockServiceFindAll(adjustsList);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/adjusts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Ajuste")
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
        @DisplayName("findById returns STATUS CODE 200 and a adjust when successful")
        void findById_ReturnsStatusCode200AndAdjust_WhenSuccessful() throws Exception {

            AdjustDTO adjustDTO = createValidAdjustDTO(UUID.randomUUID());

            BDDMockito.when(adjustServiceMock.findById(Mockito.any(UUID.class)))
                    .thenReturn(adjustDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/adjusts/{id}", adjustDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            result -> {
                                AdjustDTO adjustDTOFound = objectMapper.readValue(
                                        result.getResponse().getContentAsString(),
                                        AdjustDTO.class
                                );

                                assertEquals(adjustDTO.getId(), adjustDTOFound.getId());
                                assertEquals(adjustDTO.getName(), adjustDTOFound.getName());
                                assertEquals(adjustDTO.getCost(), adjustDTOFound.getCost());
                                assertEquals(adjustDTO.getIsActive(), adjustDTOFound.getIsActive());
                            }
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("findById returns STATUS CODE 404 when adjust is not found")
        void findById_ReturnsStatusCode404_WhenAdjustIsNotFound() throws Exception {

            BDDMockito.when(adjustServiceMock.findById(Mockito.any(UUID.class)))
                    .thenThrow(new EntityNotFoundException("The given adjust does not exist"));

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/adjusts/{id}", UUID.randomUUID())
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

    @DisplayName("Test addAdjust method")
    @Nested
    class AddAdjustTests {

        @Test
        @DisplayName("addAdjust returns STATUS CODE 201 and adjust when successful")
        void addAdjust_ReturnsStatusCode201AndAdjust_WhenSuccessful() throws Exception {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            AdjustDTO adjustDTO = createValidAdjustDTO(UUID.randomUUID());

            BDDMockito.when(adjustServiceMock.addAdjust(Mockito.any(SaveUpdateAdjustDTO.class)))
                    .thenReturn(adjustDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/adjusts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateAdjustDTO))
            );

            String expectedLocation = String.format("http://localhost/adjusts/%s", adjustDTO.getId());

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isCreated(),
                            MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(String.valueOf(adjustDTO.getId())), UUID.class),
                            MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(adjustDTO.getName()), String.class),
                            MockMvcResultMatchers.jsonPath("$.cost", CoreMatchers.is(String.valueOf(adjustDTO.getCost())), String.class),
                            MockMvcResultMatchers.jsonPath("$.isActive", CoreMatchers.is(String.valueOf(adjustDTO.getIsActive())), String.class),

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
        @DisplayName("addAdjust returns STATUS CODE 400 when adjust is null")
        void addAdjust_ReturnsStatusCode400_WhenAdjustIsNull() throws Exception {

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/adjusts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(null))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addAdjust returns STATUS CODE 400 when adjust name is null")
        void addAdjust_ReturnsStatusCode400_WhenAdjustNameIsNull() throws Exception {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setName(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/adjusts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateAdjustDTO))
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
        @DisplayName("addAdjust returns STATUS CODE 400 when adjust cost lesser than 0.01 cent")
        void addAdjust_ReturnsStatusCode400_WhenAdjustCostIsLesserThan1Cent() throws Exception {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setCost(0.0);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/adjusts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateAdjustDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("cost")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given cost cannot be lesser than 0.01"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addAdjust returns STATUS CODE 400 when adjust cost is negative")
        void addAdjust_ReturnsStatusCode400_WhenAdjustCostIsNegative() throws Exception {
            SaveUpdateAdjustDTO saveUpdateAdjustDTO = createValidSaveUpdateAdjustDTO();
            saveUpdateAdjustDTO.setCost(-1.0);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/adjusts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateAdjustDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("cost")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given cost cannot be lesser than 0.01"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }
}