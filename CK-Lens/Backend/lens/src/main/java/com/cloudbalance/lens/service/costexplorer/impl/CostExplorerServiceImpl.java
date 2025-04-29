package com.cloudbalance.lens.service.costexplorer.impl;

import com.cloudbalance.lens.dto.costexplorer.CostExplorerRequestDTO;
import com.cloudbalance.lens.dto.costexplorer.CostExplorerResponseDTO;
import com.cloudbalance.lens.dto.costexplorer.DisplayNameDTO;
import com.cloudbalance.lens.entity.ColumnName;
import com.cloudbalance.lens.exception.BadRequestException;
import com.cloudbalance.lens.exception.CustomException;
import com.cloudbalance.lens.exception.ResourceNotFoundException;
import com.cloudbalance.lens.repository.AccountRepository;
import com.cloudbalance.lens.repository.ColumnNameRepository;
import com.cloudbalance.lens.service.costexplorer.CostExplorerService;
import com.cloudbalance.lens.utils.Constant;
import com.cloudbalance.lens.utils.SnowflakeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class CostExplorerServiceImpl implements CostExplorerService {

    private final SnowflakeRepository snowflakeRepository;
    private final ColumnNameRepository columnNameRepository;
    private final AccountRepository accountRepository;

    public CostExplorerServiceImpl(SnowflakeRepository snowflakeRepository,
                                   ColumnNameRepository columnNameRepository,
                                   AccountRepository accountRepository) {
        this.snowflakeRepository = snowflakeRepository;
        this.columnNameRepository = columnNameRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<DisplayNameDTO> getDisplayName() {
        return columnNameRepository.getAllDisplayName();
    }

    @Override
    public List<String> getFilter(String fieldName) {

        if (!fieldName.matches("^[a-zA-Z_-][a-zA-Z0-9_-]*$")) {
            throw new CustomException.InvalidArgumentsException("Invalid column name format: {}"+ fieldName);
        }
        ColumnName columnName = columnNameRepository.findByFieldName(fieldName)
                .orElseThrow(() -> new ResourceNotFoundException(Constant.COLUMN_NAME_NOT_FOUND + fieldName));
        return snowflakeRepository.getFilter(columnName.getNameOfColumn());
    }

    @Override
    public CostExplorerResponseDTO fetchDate(CostExplorerRequestDTO costExplorerRequestDTO) {
        accountRepository.findByAccountNumber(Long.valueOf(costExplorerRequestDTO.getAccountId()))
                .orElseThrow(() -> {
                    log.warn("Account not found with account number: {}", costExplorerRequestDTO.getAccountId());
                    return new ResourceNotFoundException(Constant.ACCOUNT_NOT_FOUND_WITH_ACCOUNT_ID +
                            costExplorerRequestDTO.getAccountId());
                });

        ColumnName columnName = columnNameRepository.findByFieldName(costExplorerRequestDTO.getGroupBy())
                .orElseThrow(() -> {
                    log.warn("Column not found for field name {}", costExplorerRequestDTO.getGroupBy());
                    return new ResourceNotFoundException(Constant.COLUMN_NAME_NOT_FOUND +
                            costExplorerRequestDTO.getGroupBy());
                });

        LocalDate startDate = LocalDate.parse(costExplorerRequestDTO.getStartDate());
        LocalDate endDate = LocalDate.parse(costExplorerRequestDTO.getEndDate());

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be later than end date");
        }

        List<CostExplorerResponseDTO.CostExplorerData> data = snowflakeRepository.getData(
                costExplorerRequestDTO.getAccountId(),
                columnName.getNameOfColumn(),
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

