package com.cloudbalance.lens.dto.costexplorer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnFilterDTO {
    private List<ColumnFilter> filters;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnFilter {
        private String columnName;
        private List<String> filterValues;
    }
}
