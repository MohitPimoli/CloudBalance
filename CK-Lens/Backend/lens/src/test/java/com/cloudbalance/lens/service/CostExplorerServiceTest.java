package com.cloudbalance.lens.service;

import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequestDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;
import com.cloudbalance.lens.entity.Account;
import com.cloudbalance.lens.entity.ColumnName;
import com.cloudbalance.lens.exception.BadRequestException;
import com.cloudbalance.lens.exception.ResourceNotFoundException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.repository.ColumnNameRepository;
import com.cloudbalance.lens.service.costexplorer.impl.CostExplorerServiceImpl;
import com.cloudbalance.lens.utils.SnowflakeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CostExplorerServiceTest {

    @Mock
    private SnowflakeRepository snowflakeRepository;

    @Mock
    private ColumnNameRepository columnNameRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CostExplorerServiceImpl costExplorerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDisplayName_ShouldReturnDisplayNames() {
        List<DisplayNameDTO> expected = List.of(new DisplayNameDTO("service", "Service"));

        when(columnNameRepository.getAllDisplayName()).thenReturn(expected);

        List<DisplayNameDTO> result = costExplorerService.getDisplayName();

        assertEquals(expected, result);
        verify(columnNameRepository).getAllDisplayName();
    }

    @Test
    void testGetFilter_ValidFieldName_ShouldReturnFilterValues() {
        String fieldName = "service";
        ColumnName columnName = ColumnName.builder().fieldName(fieldName).nameOfColumn("SERVICE").build();

        when(columnNameRepository.findByFieldName(fieldName)).thenReturn(Optional.of(columnName));
        when(snowflakeRepository.getFilter("SERVICE")).thenReturn(List.of("EC2", "S3"));

        List<String> result = costExplorerService.getFilter(fieldName);

        assertEquals(List.of("EC2", "S3"), result);
        verify(snowflakeRepository).getFilter("SERVICE");
    }

    @Test
    void testGetFilter_InvalidFieldName_ShouldThrowException() {
        String invalidField = "123-invalid";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            costExplorerService.getFilter(invalidField);
        });

        assertTrue(exception.getMessage().contains("Invalid column name format"));
    }

    @Test
    void testFetchDate_ValidInput_ShouldReturnData() {
        CostExplorerRequestDTO dto = CostExplorerRequestDTO.builder()
                .accountId("1234567890")
                .groupBy("service")
                .startDate("2023-01-01")
                .endDate("2023-01-31")
                .build();

        ColumnName columnName = ColumnName.builder().fieldName("service").nameOfColumn("SERVICE").build();
        Account account = new Account(); // Simplify if Account has fields

        when(accountRepository.findByAccountNumber(1234567890L)).thenReturn(Optional.of(account));
        when(columnNameRepository.findByFieldName("service")).thenReturn(Optional.of(columnName));
        when(snowflakeRepository.getData(any(), any(), any(), any(), any()))
                .thenReturn(List.of(new CostExplorerResponseDTO.CostExplorerData()));

        CostExplorerResponseDTO response = costExplorerService.fetchDate(dto);

        assertEquals("1234567890", response.getAccountId());
        assertEquals("service", response.getGroupBy());
        assertEquals("2023-01-01", response.getStartDate());
        assertEquals("2023-01-31", response.getEndDate());
        assertEquals("Success", response.getMessage());
    }

    @Test
    void testFetchDate_InvalidDates_ShouldThrowBadRequestException() {
        CostExplorerRequestDTO dto = CostExplorerRequestDTO.builder()
                .accountId("1234567890")
                .groupBy("service")
                .startDate("2023-02-01")
                .endDate("2023-01-01")
                .build();

        Account account = new Account();
        ColumnName columnName = ColumnName.builder().fieldName("service").nameOfColumn("SERVICE").build();

        when(accountRepository.findByAccountNumber(1234567890L)).thenReturn(Optional.of(account));
        when(columnNameRepository.findByFieldName("service")).thenReturn(Optional.of(columnName));

        assertThrows(BadRequestException.class, () -> costExplorerService.fetchDate(dto));
    }

    @Test
    void testFetchDate_AccountNotFound_ShouldThrowException() {
        CostExplorerRequestDTO dto = CostExplorerRequestDTO.builder()
                .accountId("1234567890")
                .groupBy("service")
                .startDate("2023-01-01")
                .endDate("2023-01-31")
                .build();

        when(accountRepository.findByAccountNumber(1234567890L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> costExplorerService.fetchDate(dto));
    }

    @Test
    void testFetchDate_ColumnNotFound_ShouldThrowException() {
        CostExplorerRequestDTO dto = CostExplorerRequestDTO.builder()
                .accountId("1234567890")
                .groupBy("service")
                .startDate("2023-01-01")
                .endDate("2023-01-31")
                .build();

        when(accountRepository.findByAccountNumber(1234567890L)).thenReturn(Optional.of(new Account()));
        when(columnNameRepository.findByFieldName("service")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> costExplorerService.fetchDate(dto));
    }
}
