package com.meuatelieweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.MeasureController;
import com.meuatelieweb.backend.domain.measure.MeasureService;
import com.meuatelieweb.backend.domain.measure.dto.MeasureDTO;
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

import static com.meuatelieweb.backend.domain.measure.MeasureCreator.createValidMeasureDTO;

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

}
