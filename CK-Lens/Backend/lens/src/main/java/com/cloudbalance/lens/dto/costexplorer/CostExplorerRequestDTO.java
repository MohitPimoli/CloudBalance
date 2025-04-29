package com.cloudbalance.lens.dto.costexplorer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Account ID is mandatory")
    @Pattern(regexp = "^[0-9]{12}$", message = "Account ID must be a 12-digit number")
    private String accountId;

    @NotBlank(message = "GroupBy is mandatory")
    @Pattern(regexp = "^[a-z\\-]+$", message = "GroupBy can only contain lowercase letters and hyphens")
    private String groupBy;

    @Pattern(
            regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "Start date must be in format yyyy-MM-dd"
    )
    private String startDate;

    @Pattern(
            regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "End date must be in format yyyy-MM-dd"
    )
    private String endDate;
    private ColumnFilterDTO filterDTO;
}
