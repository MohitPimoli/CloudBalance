package com.cloudbalance.lens.dto.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignAccountResponse {
    private Long accountId;
    private Long accountNumber;
    private String accountHolderName;
    private boolean linked;
}
