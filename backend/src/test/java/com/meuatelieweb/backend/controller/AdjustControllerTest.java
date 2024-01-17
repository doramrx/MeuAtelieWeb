package com.meuatelieweb.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meuatelieweb.backend.controllers.AdjustController;
import com.meuatelieweb.backend.controllers.AdjustController;
import com.meuatelieweb.backend.domain.adjust.AdjustService;
import com.meuatelieweb.backend.domain.adjust.dto.AdjustDTO;
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
}
