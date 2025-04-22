package com.cloudbalance.lens.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String arn;
    private String accountHolderName;
    private Long accountNumber;
    private boolean orphan;
}

