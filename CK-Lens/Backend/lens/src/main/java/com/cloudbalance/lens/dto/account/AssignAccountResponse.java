package com.cloudbalance.lens.dto.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignAccountResponse {
    private Long accountId;
    private Long accountNumber;
    private String accountHolderName;
    private boolean linked;
}
