package com.cloudbalance.lens.dto.costexplorer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnFilterDTO {

    @NotEmpty(message = "Filters must not be empty")
    @Valid
    private List<ColumnFilter> filters;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnFilter {

        @NotBlank(message = "Column name must not be blank")
        @Pattern(regexp = "^[a-z\\-]+$", message = "Column name can only contain lowercase letters and hyphens")
        private String columnName;

        @NotEmpty(message = "Filter values must not be empty when column name is provided")
        private List<
                @Pattern(
                        regexp = "^[A-Za-z0-9\\-_]+$",
                        message = "Each filter value can only contain letters, numbers, hyphens, and underscores"
                )
                        String
                > filterValues;
    }

}
