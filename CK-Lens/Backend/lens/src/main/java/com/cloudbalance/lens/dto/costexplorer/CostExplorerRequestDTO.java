package com.cloudbalance.lens.dto.costexplorer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class CostExplorerRequestDTO {
    private String accountId;
    private String groupBy;
    private String startDate;
    private String endDate;
    private ColumnFilterDTO filterDTO;
}
