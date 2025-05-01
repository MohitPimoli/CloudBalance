package com.cloudbalance.lens.dto.usermanagement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusDTO {
    private Long active;
    private Long all;
}
