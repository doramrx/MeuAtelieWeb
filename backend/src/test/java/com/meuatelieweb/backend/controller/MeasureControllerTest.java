package com.meuatelieweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.MeasureController;
import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
import com.meuatelieweb.backend.domain.measure.MeasureService;
import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
import com.meuatelieweb.backend.domain.measure.dto.SaveUpdateMeasureDTO;
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

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.createValidCustomerDTO;
import static com.meuatelieweb.backend.domain.customer.CustomerCreator.createValidUpdateCustomerDTO;
import static com.meuatelieweb.backend.domain.measure.MeasureCreator.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = {MeasureController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Measure Controller")
class MeasureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private MeasureController measureController;

    @MockBean
    private MeasureService measureServiceMock;

    @DisplayName("Test findAll method")
    @Nested
    class FindAllTests {

        private void mockServiceFindAll(Page<MeasureDTO> measuresList) {
            BDDMockito.when(measureServiceMock.findAll(
                    Mockito.any(Pageable.class),
                    Mockito.anyString(),
                    Mockito.anyBoolean())
            ).thenReturn(measuresList);
        }

        @Test
        @DisplayName("findAll returns STATUS CODE 200 and a page of measures when successful")
        void findAll_ReturnsStatusCode200AndPageOfMeasures_WhenSuccessful() throws Exception {

            List<MeasureDTO> measureDTOList = List.of(
                    createValidMeasureDTO(UUID.randomUUID()),
                    createValidMeasureDTO(UUID.randomUUID())
            );

            Page<MeasureDTO> measuresList = new PageImpl<>(
                    measureDTOList,
                    Pageable.ofSize(20),
                    20);

            this.mockServiceFindAll(measuresList);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/measures")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Cintura")
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
        @DisplayName("findAll returns STATUS CODE 200 and empty page of measures when measures do not exist")
        void findAll_ReturnsStatusCode200AndEmptyPageOfMeasures_WhenMeasuresDoNotExist() throws Exception {

            Page<MeasureDTO> measuresList = new PageImpl<>(
                    List.of(),
                    Pageable.ofSize(20),
                    20);

            this.mockServiceFindAll(measuresList);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/measures")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Cintura")
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


    @DisplayName("Test findAll method")
    @Nested
    class FindByIdTests {

        @Test
        @DisplayName("findById returns STATUS CODE 200 and a measure when successful")
        void findById_ReturnsStatusCode200AndMeasure_WhenSuccessful() throws Exception {

            MeasureDTO measureDTO = createValidMeasureDTO(UUID.randomUUID());

            BDDMockito.when(measureServiceMock.findById(Mockito.any(UUID.class)))
                    .thenReturn(measureDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/measures/{id}", measureDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            result -> {
                                MeasureDTO measureDTOFound = objectMapper.readValue(
                                        result.getResponse().getContentAsString(),
                                        MeasureDTO.class
                                );

                                assertEquals(measureDTO.getId(), measureDTOFound.getId());
                                assertEquals(measureDTO.getName(), measureDTOFound.getName());
                                assertEquals(measureDTO.getIsActive(), measureDTOFound.getIsActive());
                            }
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("findById returns STATUS CODE 404 when measure is not found")
        void findById_ReturnsStatusCode404_WhenMeasureIsNotFound() throws Exception {

            BDDMockito.when(measureServiceMock.findById(Mockito.any(UUID.class)))
                    .thenThrow(new EntityNotFoundException("The given measure does not exist"));

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/measures/{id}", UUID.randomUUID())
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

    @DisplayName("Test addMeasure method")
    @Nested
    class AddMeasureTests {

        @Test
        @DisplayName("addMeasure returns STATUS CODE 201 and measure when successful")
        void addMeasure_ReturnsStatusCode201AndMeasure_WhenSuccessful() throws Exception {
            SaveUpdateMeasureDTO saveUpdateMeasureDTO = createValidSaveUpdateMeasureDTO();
            MeasureDTO measureDTO = createValidMeasureDTO(UUID.randomUUID());

            BDDMockito.when(measureServiceMock.addMeasure(Mockito.any(SaveUpdateMeasureDTO.class)))
                    .thenReturn(measureDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/measures")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateMeasureDTO))
            );

            String expectedLocation = String.format("http://localhost/measures/%s", measureDTO.getId());

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isCreated(),
                            MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(String.valueOf(measureDTO.getId())), UUID.class),
                            MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(measureDTO.getName()), String.class),
                            MockMvcResultMatchers.jsonPath("$.isActive", CoreMatchers.is(String.valueOf(measureDTO.getIsActive())), String.class),

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
        @DisplayName("addMeasure returns STATUS CODE 400 when measure is null")
        void addMeasure_ReturnsStatusCode400_WhenMeasureIsNull() throws Exception {

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/measures")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(null))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("addMeasure returns STATUS CODE 400 when measure name is null")
        void addMeasure_ReturnsStatusCode400_WhenMeasureNameIsNull() throws Exception {
            SaveUpdateMeasureDTO saveUpdateMeasureDTO = createValidSaveUpdateMeasureDTO();
            saveUpdateMeasureDTO.setName(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/measures")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateMeasureDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("name")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given name cannot be empty"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @DisplayName("Test updateMeasure method")
    @Nested
    class UpdateMeasureTests {

        @Test
        @DisplayName("updateMeasure returns STATUS CODE 200 and updates measure when successful")
        void updateMeasure_ReturnsStatusCode200AndUpdatesMeasure_WhenSuccessful() throws Exception {
            SaveUpdateMeasureDTO saveUpdateMeasureDTO = createValidSaveUpdateMeasureDTO();
            MeasureDTO measureDTO = createValidMeasureDTO(UUID.randomUUID());

            BDDMockito.when(measureServiceMock.updateMeasure(Mockito.any(UUID.class), Mockito.any(SaveUpdateMeasureDTO.class)))
                    .thenReturn(measureDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/measures/{id}", measureDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateMeasureDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isOk(),
                            MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(String.valueOf(measureDTO.getId()))),
                            MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(saveUpdateMeasureDTO.getName())),
                            MockMvcResultMatchers.jsonPath("$.isActive", CoreMatchers.is(measureDTO.getIsActive()))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("updateMeasure returns STATUS CODE 400 when measure is null")
        void updateMeasure_ReturnsStatusCode400_WhenMeasureIsNull() throws Exception {
            MeasureDTO measureDTO = createValidMeasureDTO(UUID.randomUUID());

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/measures/{id}", measureDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(null))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("updateMeasure returns STATUS CODE 404 when measure is not found")
        void updateMeasure_ReturnsStatusCode404_WhenMeasureIsNotFound() throws Exception {

            BDDMockito.when(measureServiceMock.updateMeasure(Mockito.any(UUID.class), Mockito.any(SaveUpdateMeasureDTO.class)))
                    .thenThrow(EntityNotFoundException.class);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/measures/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createValidSaveUpdateMeasureDTO()))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isNotFound(),
                            MockMvcResultMatchers.content().string(org.hamcrest.Matchers.emptyOrNullString())
                    )
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("updateMeasure returns STATUS CODE 400 when measure name is null")
        void updateMeasure_ReturnsStatusCode400_WhenMeasureNameIsNull() throws Exception {
            SaveUpdateMeasureDTO saveUpdateMeasureDTO = createValidSaveUpdateMeasureDTO();
            saveUpdateMeasureDTO.setName(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/measures/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(saveUpdateMeasureDTO))
            );

            response
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].field", CoreMatchers.is("name")),
                            MockMvcResultMatchers.jsonPath("$.invalidFields[0].message", CoreMatchers.is("The given name cannot be empty"))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }
    }

}