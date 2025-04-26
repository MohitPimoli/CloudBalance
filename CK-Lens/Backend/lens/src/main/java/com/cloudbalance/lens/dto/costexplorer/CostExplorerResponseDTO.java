package com.cloudbalance.lens.dto.costexplorer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CostExplorerResponseDTO {
    private List<CostExplorerData> data;
    private String groupBy;
    private String startDate;
    private String endDate;
    private String accountId;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CostExplorerData {
        private String groupBy;
        private Double cost;
        private String date;
    }
}
