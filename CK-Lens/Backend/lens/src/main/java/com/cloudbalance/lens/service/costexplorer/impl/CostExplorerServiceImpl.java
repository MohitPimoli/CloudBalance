package com.cloudbalance.lens.service.costexplorer.impl;

import com.cloudbalance.lens.dto.costexplorer.ColumnFilterDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequestDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;
import com.cloudbalance.lens.entity.ColumnName;
import com.cloudbalance.lens.exception.BadRequestException;
import com.cloudbalance.lens.exception.ResourceNotFoundException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.repository.ColumnNameRepository;
import com.cloudbalance.lens.service.costexplorer.CostExplorerService;
import com.cloudbalance.lens.utils.SnowflakeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


@Service
@Slf4j
public class CostExplorerServiceImpl implements CostExplorerService {

    @Autowired
    private SnowflakeRepository snowflakeRepository;
    @Autowired
    private ColumnNameRepository columnNameRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<DisplayNameDTO> getDisplayName() {
        return columnNameRepository.getAllDisplayName();
    }

    @Override
    public List<String> getFilter(String fieldName) {

        if (!fieldName.matches("^[a-zA-Z_-][a-zA-Z0-9_-]*$")) {
            throw new IllegalArgumentException("Invalid column name format");
        }
        ColumnName columnName = columnNameRepository.findByFieldName(fieldName)
                .orElseThrow(() -> new ResourceNotFoundException("Column Name not found for FieldName: " + fieldName));
        return snowflakeRepository.getFilter(columnName.getColumnName());
    }

    @Override
    public CostExplorerResponseDTO fetchDate(CostExplorerRequestDTO costExplorerRequestDTO) {
        accountRepository.findByAccountNumber(Long.valueOf(costExplorerRequestDTO.getAccountId()))
                .orElseThrow(() -> {
                    log.warn("Account not found with account number: {}", costExplorerRequestDTO.getAccountId());
                    return new ResourceNotFoundException("Account not found with AccountNumber: " + costExplorerRequestDTO.getAccountId());
                });

        ColumnName columnName = columnNameRepository.findByFieldName(costExplorerRequestDTO.getGroupBy())
                .orElseThrow(() -> {
                    log.warn("Column not found for field name {}", costExplorerRequestDTO.getGroupBy());
                    return new ResourceNotFoundException("Column not found for field name: " + costExplorerRequestDTO.getGroupBy());
                });

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try {
            if (costExplorerRequestDTO.getStartDate() != null && !costExplorerRequestDTO.getStartDate().trim().isEmpty()) {
                LocalDate.parse(costExplorerRequestDTO.getStartDate(), formatter);
            }

            if (costExplorerRequestDTO.getEndDate() != null && !costExplorerRequestDTO.getEndDate().trim().isEmpty()) {
                LocalDate.parse(costExplorerRequestDTO.getEndDate(), formatter);
            }
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Invalid date format. Expected format is YYYY-MM-DD");
        }

        if (costExplorerRequestDTO.getFilterDTO() != null && costExplorerRequestDTO.getFilterDTO().getFilters() != null) {
            if (!costExplorerRequestDTO.getFilterDTO().getFilters().isEmpty()) {
                for (ColumnFilterDTO.ColumnFilter filter : costExplorerRequestDTO.getFilterDTO().getFilters()) {
                    if (filter.getFilterValues() == null || filter.getFilterValues().isEmpty()) {
                        throw new BadRequestException("Filter values cannot be empty for column: " + filter.getColumnName());
                    }
                }
            }
        }

        List<CostExplorerResponseDTO.CostExplorerData> data = snowflakeRepository.getData(
                costExplorerRequestDTO.getAccountId(),
                columnName.getColumnName(),
                costExplorerRequestDTO.getStartDate(),
                costExplorerRequestDTO.getEndDate(),
                costExplorerRequestDTO.getFilterDTO()
        );

        return CostExplorerResponseDTO.builder()
                .data(data)
                .groupBy(costExplorerRequestDTO.getGroupBy())
                .startDate(costExplorerRequestDTO.getStartDate())
                .endDate(costExplorerRequestDTO.getEndDate())
                .accountId(costExplorerRequestDTO.getAccountId())
                .message("Success")
                .build();
    }

}






