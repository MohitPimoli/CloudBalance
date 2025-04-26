package com.cloudbalance.lens.dto.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {
    private Long id;
    private String arn;
    private String accountHolderName;
    private Long accountNumber;
    private boolean orphan;
}

