package com.cloudbalance.lens.controller;

import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequestDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;
import com.cloudbalance.lens.service.costexplorer.CostExplorerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CostExplorerController.class)
class CostExplorerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CostExplorerService costExplorerService;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private CostExplorerController costExplorerController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDistinctValues() throws Exception {
        List<String> mockValues = List.of("Region1", "Region2");
        when(costExplorerService.getFilter("region")).thenReturn(mockValues);

        mockMvc.perform(get("/cost/filter")
                        .param("fieldName", "region"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Region1"));
    }

    @Test
    void testGetDisplayNames() throws Exception {
        List<DisplayNameDTO> displayNames = List.of(
                DisplayNameDTO.builder().fieldName("service").displayName("Service").build()
        );

        when(costExplorerService.getDisplayName()).thenReturn(displayNames);

        mockMvc.perform(get("/cost/display-names"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].field").value("service"))
                .andExpect(jsonPath("$[0].displayName").value("Service"));
    }

    @Test
    void testGetData() throws Exception {
        CostExplorerRequestDTO requestDTO = CostExplorerRequestDTO.builder()
                .accountId("123456789012")
                .startDate("2024-01-01")
                .endDate("2024-01-31")
                .build();

        CostExplorerResponseDTO.CostExplorerData dataItem = new CostExplorerResponseDTO.CostExplorerData(
                "EC2", 150.75, "2024-01-15"
        );

        CostExplorerResponseDTO responseDTO = CostExplorerResponseDTO.builder()
                .data(List.of(dataItem))
                .groupBy("service")
                .startDate("2024-01-01")
                .endDate("2024-01-31")
                .accountId("123456789012")
                .message("Success")
                .build();

        when(costExplorerService.fetchDate(requestDTO)).thenReturn(responseDTO);

        mockMvc.perform(post("/cost/data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].groupBy").value("EC2"))
                .andExpect(jsonPath("$.data[0].cost").value(150.75))
                .andExpect(jsonPath("$.data[0].date").value("2024-01-15"))
                .andExpect(jsonPath("$.groupBy").value("service"))
                .andExpect(jsonPath("$.startDate").value("2024-01-01"))
                .andExpect(jsonPath("$.endDate").value("2024-01-31"))
                .andExpect(jsonPath("$.accountId").value("123456789012"))
                .andExpect(jsonPath("$.message").value("Success"));
    }

}
